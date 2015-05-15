/**
 * 
 */
package co.pishfa.accelerate.async;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Indicates that this method should be run in a different thread. If the parameters of the method is annotated
 * with any of {@link co.pishfa.accelerate.schedule.ScheduleStart}, {@link co.pishfa.accelerate.schedule.ScheduleDelay},
 * {@link co.pishfa.accelerate.schedule.ScheduleInterval}, and {@link co.pishfa.accelerate.schedule.SchedulePattern}.
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
public @interface Async {

    public long delay() default 0;

    public long interval() default 0;

    /**
     * @return Specifies if a job with the same name exists, how scheduler behave, default is to create a new job.
     */
    @Nonbinding
    public RescheduleType reschedule() default RescheduleType.NEW;

}
