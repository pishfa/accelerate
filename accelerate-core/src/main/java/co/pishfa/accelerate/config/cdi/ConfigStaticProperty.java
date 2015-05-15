/**
 * 
 */
package co.pishfa.accelerate.config.cdi;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Specifies one property of configuration. Note that since this property is injected as dependent scope,
 * if the underlying configuration changes during the lifecycle of declaring object, this value of this property remains the
 * same.
 * 
 * @author Taha Ghasemi
 * 
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigStaticProperty {
	/**
	 * @return The configuration key. If this reader is used inside a class annotated with {@link co.pishfa.accelerate.config.ConfigEntity}
     * its alias will append to this value. Defaults to the name of property, if any.
	 */
	@Nonbinding
	String value() default "";
}
