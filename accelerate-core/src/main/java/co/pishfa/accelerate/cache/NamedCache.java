/**
 * 
 */
package co.pishfa.accelerate.cache;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Specifies a cache with a specific name.
 * 
 * @author Taha Ghasemi
 * 
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamedCache {
	/**
	 * The cache name. If no name is specified, the name of target member which this annotation is specified on, will be
	 * used as the name.
	 * 
	 * @return
	 */
	@Nonbinding
	String value() default "";

}
