/**
 *
 */
package co.pishfa.accelerate.schedule;

import co.pishfa.accelerate.async.RescheduleType;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.core.FrameworkShutdownEvent;
import co.pishfa.accelerate.core.FrameworkStartedEvent;
import co.pishfa.accelerate.persistence.DbService;
import co.pishfa.accelerate.resource.ResourceUtils;
import co.pishfa.accelerate.service.Service;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Taha Ghasemi TODO add support of @Scheduled and @Every
 */
@Service
public class SchedulerService {

    private static final String EVERY_MONTH_CRON = "0 0 0 1 * ?";
    public static final String QUARTZ_PERSISTENT_PROPERTIES = "quartz.persistent.properties";
    public static final String OBSERVER_NAME = "observer.name";
    public static final String EVERY_MONTH = "every.month";

    public static class EventFiringJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            CdiUtils.getBeanManager().fireEvent(new ScheduleTrigger(), new ScheduledLiteral((String) context.get(OBSERVER_NAME)));
        }
    }


    @Inject
    private Logger log;

    @Inject
    @Scheduled("")
    private Event<ScheduleTrigger> every;

    private Scheduler inMemoryScheduler;
    private Scheduler persistedScheduler;

    @Inject
    private DbService dbService;

    public static SchedulerService getInstance() {
        return CdiUtils.getInstance(SchedulerService.class);
    }

    @PostConstruct
    public void init() {
        if (ResourceUtils.getResource(QUARTZ_PERSISTENT_PROPERTIES) != null) {
            try {
                //be sure that quartz tables are created
                dbService.getDefaultEntityManager();
                persistedScheduler = new StdSchedulerFactory(QUARTZ_PERSISTENT_PROPERTIES).getScheduler();
                schedule(EVERY_MONTH, RescheduleType.SKIP, EVERY_MONTH_CRON);
            } catch (SchedulerException e) {
                log.error("", e);
            }
        }
    }

    public void startup(@Observes FrameworkStartedEvent event) throws SchedulerException {
        if (inMemoryScheduler != null)
            inMemoryScheduler.start();
        if (persistedScheduler != null)
            persistedScheduler.start();
    }

    public void shutdown(@Observes final FrameworkShutdownEvent event) throws SchedulerException {
        if (inMemoryScheduler != null)
            inMemoryScheduler.shutdown();
        if (persistedScheduler != null)
            persistedScheduler.shutdown();
    }

    @Produces
    @ApplicationScoped
    @InMemory
    public Scheduler getInMemoryScheduler() throws SchedulerException {
        if (inMemoryScheduler == null) {
            inMemoryScheduler = StdSchedulerFactory.getDefaultScheduler();
        }
        return inMemoryScheduler;
    }

    @Produces
    @ApplicationScoped
    @Persisted
    public Scheduler getPersistedScheduler() throws SchedulerException {
        return persistedScheduler == null ? getInMemoryScheduler() : persistedScheduler;
    }

    /**
     * Schedules a persistent schedule using the provided cron expression. To notify, an event will be fired.
     */
    public void schedule(String name, RescheduleType rescheduleType, String cron) throws SchedulerException {
        JobKey jobKey = new JobKey(name);
        if (persistedScheduler.getJobDetail(jobKey) != null) {
            if (rescheduleType == RescheduleType.SKIP)
                return;
            if (rescheduleType == RescheduleType.DELETE_PREV)
                persistedScheduler.deleteJob(jobKey);
        }

        JobBuilder jobBuilder = JobBuilder.newJob().ofType(EventFiringJob.class).usingJobData(OBSERVER_NAME, name);
        JobDetail jobDetail = rescheduleType==RescheduleType.NEW? jobBuilder.build() : jobBuilder.withIdentity(jobKey).build();
        Trigger trigger = TriggerBuilder.newTrigger().startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        persistedScheduler.scheduleJob(jobDetail, trigger);
    }

}
