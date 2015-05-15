/**
 * 
 */
package co.pishfa.accelerate.ui;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import co.pishfa.accelerate.message.UserMessageSeverity;

/**
 * Displays a user message when the method successfully completed. If the returned result is not-null, it also orders
 * the keep message. The method arguments are passed as parameters of the message.
 * 
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
@Inherited
public @interface UiMessage {

	/**
	 * @return the resource bundle key of message to be displayed. If nothing is specified, the
	 *         "controller."+method-name will be used.
	 */
	@Nonbinding
	String value() default "";

	@Nonbinding
	UserMessageSeverity severity() default UserMessageSeverity.INFO;

}
