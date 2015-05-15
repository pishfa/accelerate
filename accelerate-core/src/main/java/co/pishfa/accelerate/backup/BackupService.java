package co.pishfa.accelerate.backup;

import co.pishfa.accelerate.async.Async;
import co.pishfa.accelerate.async.RescheduleType;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.schedule.*;
import co.pishfa.accelerate.storage.service.FileService;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.security.service.RunAs;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.quartz.SchedulerException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static co.pishfa.accelerate.utility.TimeUtils.*;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class BackupService extends BaseEntityService<Backup, Long> {

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

    @Override
    public EntityRepository<Backup,Long> getRepository() {
        return backupRepository;
    }

    public void onConfig(@Observes ConfigAppliedEvent event) throws SchedulerException {
        BackupConfig newConfig = event.getConfig().getObject(BackupConfig.class);
        boolean changed = backupConfig != null && !backupConfig.getSchedule().equals(newConfig);
        schedulerService.schedule("backup", changed? RescheduleType.DELETE_PREV:RescheduleType.SKIP, newConfig.getSchedule());
        backupConfig = newConfig;
    }

    @RunAs
    public void runPerform(@Observes @Scheduled("backup") final ScheduleTrigger t) {
        getInstance().perform();
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
        backup = backupRepository.findById(backup.getId()); //obtain the last version
        int status = -1;
        try {
            String fileName = "backup_" + System.currentTimeMillis();
            File file = new File();
            file.setName(fileName);
            file.setFolder(fileService.findFolder(backupConfig.getStorage(), "/"));
            backup.setFile(file);
            String command = StringUtils.replace(backupConfig.getExecutorCommand(), "%file%", file.getFullPath());
            Process process = Runtime.getRuntime().exec(command);
            status = process.waitFor();
        } catch (Exception e) {
            getLogger().error("", e);
        }
        backup.setStatus(status == 0 ? Backup.BackupStatus.COMPLETED : Backup.BackupStatus.IN_PROGRESS);
        backupRepository.edit(backup);
    }

    @Transactional
    public void cleanUp(@Observes @Scheduled("every.month") final ScheduleTrigger t) {
        backupRepository.deleteOlderThan(toDate(since(toMilliSecond(backupConfig.getDeletePeriod(),
                TimeUnit.DAYS))));
    }
}
