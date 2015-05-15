/**
 * 
 */
package co.pishfa.accelerate.schedule;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * Marks the method parameter that indicates the start offset of an async/scheduled method.
 * The offset should be specified in milliseconds and the parameter type should be of type long.
 * This method should be annotated with {@link co.pishfa.accelerate.async.Asynchronous}.
 * @author Taha Ghasemi
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface ScheduleDelay {

}
