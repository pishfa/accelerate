package co.pishfa.accelerate.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Caches the results of method execution into a ui scope and return the result from this ui scope in subsequent
 * requests. TODO caching null is not supported yet.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
public @interface UiCached {

	@Nonbinding
	UiScope value() default UiScope.REQUEST;

	/**
	 * @return the key which will be used as the cache key. If not specified the class name + method name will be used.
	 */
	@Nonbinding
	String key() default "";

	/**
	 * @return true if method parameters should also be appended to the key. If param is of type Entity, its id will be
	 *         appended otherwise the string representation of each param prefixed by "_" is appended to the key.
	 */
	@Nonbinding
	boolean appendParameters() default true;

}
