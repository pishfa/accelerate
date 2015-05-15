/**
 *
 */
package co.pishfa.accelerate.async;

import co.pishfa.accelerate.schedule.*;
import org.quartz.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.util.Date;

/**
 * @author Taha Ghasemi
 */
@ApplicationScoped
@Alternative
public class QuartzAsyncStrategy implements AsyncStrategy {

    @Inject
    @InMemory
    private Scheduler scheduler;

    public AsyncHandle run(InvocationContext ic) throws SchedulerException {
        Async async = ic.getMethod().getAnnotation(Async.class);
        Trigger trigger = buildTrigger(async, ic);
        JobDetail job = buildJob(async, ic);
        if (job == null)
            return null;

        // TODO should we pass request, view, and other thread locals to this new thread or not?
        scheduler.scheduleJob(job, trigger);
        //TODO add support for future and custom return value which will be notified with events
        if (AsyncHandle.class.isAssignableFrom(ic.getMethod().getReturnType())) {
            return new QuartzAsyncHandle(scheduler, job.getKey(), trigger.getKey());
        } else {
            return null;
        }
    }

    private JobDetail buildJob(Async async, InvocationContext ic) throws SchedulerException {
        JobKey jobKey = new JobKey(ic.getMethod().getName(), ic.getTarget().getClass().getName());
        if (async.reschedule() == RescheduleType.DELETE_PREV)
            scheduler.deleteJob(jobKey);
        else if (async.reschedule() == RescheduleType.SKIP) {
            JobDetail job = scheduler.getJobDetail(jobKey);
            if (job != null)
                return job;
        }
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(AsyncInterceptor.ASYNC_INVOCATION_CONTEXT, ic);
        JobBuilder jobBuilder = JobBuilder.newJob().ofType(MethodInvocationJob.class).usingJobData(dataMap);
        return async.reschedule() == RescheduleType.NEW ? jobBuilder.build() : jobBuilder.withIdentity(jobKey).build();
    }

    protected Trigger buildTrigger(Async async, InvocationContext ic) {
        Trigger trigger = null;
        Date start = new Date(System.currentTimeMillis() + async.delay());
        if (ic.getParameters() != null && ic.getParameters().length > 0) {
            int paramIndex = 0;
            Annotation[][] parametersAnnotations = ic.getMethod().getParameterAnnotations();
            for (Object parameter : ic.getParameters()) {
                Annotation[] parameterAnnotations = parametersAnnotations[paramIndex];
                if (parameterAnnotations.length > 0) {
                    if (parameterAnnotations[0] instanceof ScheduleStart) {
                        if (parameter instanceof Date)
                            start = (Date) parameter;
                        else
                            start = new Date((long) parameter);
                        break;
                    } else if (parameterAnnotations[0] instanceof ScheduleDelay) {
                        start = new Date(System.currentTimeMillis() + (long) parameter);
                        break;
                    } else if (parameterAnnotations[0] instanceof ScheduleInterval) {
                        trigger = TriggerBuilder
                                .newTrigger()
                                .startAt(start)
                                .withSchedule(
                                        SimpleScheduleBuilder.simpleSchedule()
                                                .withIntervalInMilliseconds((Long) parameter).repeatForever()).build();
                        break;
                    } else if (parameterAnnotations[0] instanceof SchedulePattern) {
                        trigger = TriggerBuilder.newTrigger().startAt(start)
                                .withSchedule(CronScheduleBuilder.cronSchedule((String) parameter)).build();
                        break;
                    }
                }
                paramIndex++;
            }
        }
        if (trigger == null) {
            if (async.interval() > 0) {
                trigger = TriggerBuilder
                        .newTrigger()
                        .startAt(start)
                        .withSchedule(
                                SimpleScheduleBuilder.simpleSchedule()
                                        .withIntervalInMilliseconds(async.interval()).repeatForever()).build();
            } else {
                // immediate run, one time
                trigger = TriggerBuilder.newTrigger().startAt(start)
                        .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(1)).build();
            }
        }
        return trigger;
    }
}
