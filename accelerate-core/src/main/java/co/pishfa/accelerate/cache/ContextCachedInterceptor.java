package co.pishfa.accelerate.cache;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.pishfa.accelerate.utility.StrUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Interceptor
@ContextCached(Annotation.class)
public class ContextCachedInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CacheService cacheService;

	@AroundInvoke
	public Object invoke(InvocationContext ic) throws Exception {
		ContextCached contextCached = ic.getMethod().getAnnotation(ContextCached.class);

		Object key;
		if (StrUtils.isEmpty(contextCached.key())) {
			if (contextCached.appendParameters() && ic.getParameters() != null && ic.getParameters().length > 0) {
				key = new CacheKey(ic.getParameters());
			} else {
				key = new CacheKey(new Object[] { ic.getTarget().getClass(), ic.getMethod().getName() });
			}
		} else {
			if (contextCached.appendParameters() && ic.getParameters() != null && ic.getParameters().length > 0) {
				Object[] params = new Object[ic.getParameters().length + 1];
				System.arraycopy(ic.getParameters(), 0, params, 1, ic.getParameters().length);
				params[0] = contextCached.key();
				key = new CacheKey(params);
			} else {
				key = new CacheKey(new String[] { contextCached.key() });
			}
		}

		Cache<Object, Object> cache = cacheService.getCache(contextCached.value());

		try {
			return cache.get(key);
		} catch (IllegalArgumentException e) {
			Object result = ic.proceed();
			cache.put(key, result);
			return result;
		}
	}
}
