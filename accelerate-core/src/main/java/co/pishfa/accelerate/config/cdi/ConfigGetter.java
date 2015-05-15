/**
 * 
 */
package co.pishfa.accelerate.config.cdi;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * Returns the configuration item with the specified name. The return value of annotated method will be ignored.
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
public @interface ConfigGetter {

    /**
     * @return The configuration key. If this reader is used inside a class annotated with {@link co.pishfa.accelerate.config.ConfigEntity}
     * its alias will append to this value. Defaults to the name of method without get or is, if any.
     */
	@Nonbinding
	public String value() default "";

}
