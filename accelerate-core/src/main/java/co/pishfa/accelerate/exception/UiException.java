package co.pishfa.accelerate.exception;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated exception is intended for the user to be notified and possibly retries the action.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(ElementType.TYPE)
public @interface UiException {

	/**
	 * @return the resource key that should be displayed in case of no current is defined, or passed via the request
	 *         parameter named error to that current
	 */
	String message() default "";

	String page() default "";

}
