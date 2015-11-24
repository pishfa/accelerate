/**
 * 
 */
package co.pishfa.security.service;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Indicates that this method should be run by a different user name or in the system mode.
 * An optional action name can be specified for auditing purposes.
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
@Inherited
public @interface RunAs {

	@Nonbinding
	String username() default "";

	@Nonbinding
	boolean systemMode() default true;

	@Nonbinding
	String actionName() default "";
}
