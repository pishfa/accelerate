/**
 * 
 */
package co.pishfa.accelerate.cache;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Specifies a cache representation of ui scopes.
 * 
 * @author Taha Ghasemi
 * 
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UiCache {

	@Nonbinding
	UiScope value() default UiScope.REQUEST;
}
