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
public @interface RunAs {

	@Nonbinding
	public String username() default "";

	@Nonbinding
	public boolean systemMode() default true;

	@Nonbinding
	public String actionName() default "";
}
