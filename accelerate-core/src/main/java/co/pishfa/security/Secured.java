/**
 * 
 */
package co.pishfa.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Checks the permission of identity to perform the method. The target is the first argument of the method (if
 * available). The action is either given explicitly or constructed by "the-method-name." + :
 * <ol>
 * <li>If the method is in a EntityService then the EntityService metadata actionSet.</li>
 * <li>If the target is of type Entity use the entity metadata actionSet.</li>
 * <li>Otherwise empty string and trailing dot will be removed.</li>
 * </ol>
 * 
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
@Inherited
public @interface Secured {

	/**
	 * The action name.
	 */
	@Nonbinding
	public String value() default "";
}
