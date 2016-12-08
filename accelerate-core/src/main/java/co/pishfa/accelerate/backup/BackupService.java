package co.pishfa.accelerate.backup;

import co.pishfa.accelerate.async.Async;
import co.pishfa.accelerate.async.RescheduleType;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.schedule.ScheduleTrigger;
import co.pishfa.accelerate.schedule.Scheduled;
import co.pishfa.accelerate.schedule.SchedulerService;
import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.storage.model.Folder;
import co.pishfa.accelerate.storage.model.UploadedFile;
import co.pishfa.accelerate.storage.service.FileService;
import co.pishfa.accelerate.template.ExpressionInterpolator;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.service.AuditService;
import co.pishfa.security.service.RunAs;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.quartz.SchedulerException;
import org.slf4j.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static co.pishfa.accelerate.utility.TimeUtils.*;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class BackupService extends BaseEntityService<Backup, Long> {

    public static final String BACKUP_FAILED = "backup.failed";

    @Inject
    private Logger log;

    public static BackupService getInstance() {
        return CdiUtils.getInstance(BackupService.class);
    }

    @Inject
    private BackupRepo backupRepository;

    @Inject
    private FileService fileService;

    @Inject
    private SchedulerService schedulerService;

    private BackupConfig backupConfig;

    @Inject
    private ExpressionInterpolator expressionInterpolator;

    @Inject
    private AuditService auditService;

    @Override
    public EntityRepository<Backup,Long> getRepository() {
        return backupRepository;
    }

    public void onConfig(@Observes ConfigAppliedEvent event) throws SchedulerException {
        BackupConfig newConfig = event.getConfig().getObject(BackupConfig.class);
        boolean changed = backupConfig != null && !backupConfig.getSchedule().equals(newConfig);
        if(newConfig != null && !StrUtils.isEmpty(newConfig.getSchedule()))
            schedulerService.schedule("backup", changed? RescheduleType.DELETE_PREV:RescheduleType.SKIP, newConfig.getSchedule());
        backupConfig = newConfig;
    }

    @RunAs
    public void runPerform(@Observes @Scheduled("backup") final ScheduleTrigger t) {
        getInstance().perform();
    }

    public void performIfRequired() {
        if(backupConfig.getBackupPeriod() != null) {
            Date last = backupRepository.findLastDate(Backup.BackupStatus.COMPLETED);
            if (last == null || last.before(toDate(since(toMilliSecond(backupConfig.getBackupPeriod(), TimeUnit.SECONDS))))) {
                perform();
            }
        }
    }
    @Action
    public void perform() {
        Backup backup = new Backup();
        backup.setStatus(Backup.BackupStatus.IN_PROGRESS);
        backup = backupRepository.add(backup);
        getInstance().performBackup(backup);
    }

    @Async(reschedule = RescheduleType.DELETE_PREV, delay = 1000)
    @RunAs
    @Transactional
    public void performBackup(Backup backup) {
        for(Backup b : backupRepository.findOlderThan(toDate(since(toMilliSecond(backupConfig.getDeletePeriod(),
                TimeUnit.DAYS))))) {
            backupRepository.delete(b);
        }
        backup = backupRepository.findById(backup.getId()); //obtain the last version
        int status = -1;
        String msg = null;
        try {
            java.io.File temp = java.io.File.createTempFile("backup", "");
            Map<String, Object> params = new HashMap<>();
            params.put("file", temp.getAbsolutePath());
            String command = expressionInterpolator.populate(backupConfig.getExecutorCommand(), params);
            log.info("Backingup: " + command);
            Process process = new ProcessBuilder(command.split("###"))
                            .redirectErrorStream(true)
                            .start();
            msg = IOUtils.toString(process.getInputStream());
            log.info(msg);
            if(process.waitFor(1, TimeUnit.HOURS))
                status = process.exitValue();

            if(status == 0 && !StrUtils.isEmpty(backupConfig.getStorage())) {
                String fileName = "backup_" + System.currentTimeMillis();
                Folder out = fileService.findFolder(backupConfig.getStorage());
                backup.setFile(fileService.upload(new UploadedFile(fileName, temp), out));
            }
        } catch (Exception e) {
            getLogger().error("", e);
        }
        if(status == 0)
            backup.setStatus(Backup.BackupStatus.COMPLETED);
        else {
            backup.setStatus(Backup.BackupStatus.FAILED);
            auditService.audit(backup, BACKUP_FAILED, msg, AuditLevel.RISK);
        }
        backupRepository.edit(backup);
    }

    private String[] parseToArgs(String command) {
        List<String> res = new ArrayList<>();
        boolean insideQuote = false;
        char prev = 0;
        StringBuilder arg = new StringBuilder();
        for(char ch : command.toCharArray()) {
            if(ch == '\'' && prev != '\\') {
                if(insideQuote) {
                    insideQuote = false;
                    res.add(arg.toString());
                    arg.setLength(0);
                } else {
                    insideQuote = true;
                }
            } else if(ch == ' ' && arg.length() > 0 && !insideQuote) {
                res.add(arg.toString());
                arg.setLength(0);
            } else {
                arg.append(ch);
            }
            prev = ch;
        }
        return res.toArray(new String[0]);
    }

    /*@Transactional
    public void cleanUp(@Observes @Scheduled("every.month") final ScheduleTrigger t) {
        backupRepository.deleteOlderThan(toDate(since(toMilliSecond(backupConfig.getDeletePeriod(),
                TimeUnit.DAYS))));
    }*/
}
