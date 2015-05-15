/**
 * 
 */
package co.pishfa.accelerate.async;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 * @author Taha Ghasemi
 * 
 */
public class QuartzAsyncHandle implements AsyncHandle {

	private final Scheduler scheduler;
    private final JobKey jobKey;
	private final TriggerKey triggerKey;

	public QuartzAsyncHandle(Scheduler scheduler, JobKey jobKey, TriggerKey triggerKey) {
		this.scheduler = scheduler;
        this.jobKey = jobKey;
		this.triggerKey = triggerKey;
	}

	public void cancel() throws SchedulerException {
		scheduler.unscheduleJob(triggerKey);
	}

	public void pause() throws SchedulerException {
		scheduler.pauseTrigger(triggerKey);
	}

    public Scheduler getScheduler() {
        return scheduler;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public TriggerKey getTriggerKey() {
        return triggerKey;
    }
}
