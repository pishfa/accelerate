package co.pishfa.accelerate.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Indicates that the result of the method should be cached, and returned in the subsequent requests from cache. If
 * method has parameters, the result will be cached based on the parameter values. The cache key is always an instance
 * of {@link CacheKey}. If method has no parameters the class + the name of method will be used as the arguments to the
 * cache key otherwise the parameter values will be used. Null is also a valid result and will be cached.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
public @interface NamedCached {

	/**
	 * @return Name of global cache.
	 */
	@Nonbinding
	String value();

	/**
	 * @return the key which will be used as the cache key. If not specified and appendParamers=true and there are some
	 *         parameters, the cache key is constructed based on the parameters. If not specified and there are no
	 *         method parameters or appendParameters=false the class + method name will be used.
	 */
	@Nonbinding
	String key() default "";

	/**
	 * @return true if method parameters should also be appended to the key. The key is an instance of CacheKey.
	 */
	@Nonbinding
	boolean appendParameters() default true;

}
