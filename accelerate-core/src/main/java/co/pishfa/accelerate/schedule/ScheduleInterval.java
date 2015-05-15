/**
 * 
 */
package co.pishfa.accelerate.schedule;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
/**
 * Marks the method parameter that specifies the interval between consecutive runs of an async/scheduled method.
 * This method should be annotated with {@link co.pishfa.accelerate.async.Async}.
 * @author Taha Ghasemi
 *
 */
public @interface ScheduleInterval {

}
