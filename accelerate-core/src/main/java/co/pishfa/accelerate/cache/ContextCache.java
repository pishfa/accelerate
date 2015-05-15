/**
 * 
 */
package co.pishfa.accelerate.cache;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Specifies a cache associated to a context of a scope.
 * 
 * @author Taha Ghasemi
 * 
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ContextCache {
	/**
	 * 
	 * @return specifies the cache scope such as {@link RequestScoped}.
	 */
	@Nonbinding
	Class<? extends Annotation> value();
}
