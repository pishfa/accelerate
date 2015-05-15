package co.pishfa.accelerate.cache;

import java.io.Serializable;

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
@NamedCached("")
public class NamedCachedInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CacheService cacheService;

	@AroundInvoke
	public Object invoke(InvocationContext ic) throws Exception {
		NamedCached namedCached = ic.getMethod().getAnnotation(NamedCached.class);

		Object key;
		if (StrUtils.isEmpty(namedCached.key())) {
			if (namedCached.appendParameters() && ic.getParameters() != null && ic.getParameters().length > 0) {
				key = new CacheKey(ic.getParameters());
			} else {
				key = new CacheKey(new Object[] { ic.getTarget().getClass(), ic.getMethod().getName() });
			}
		} else {
			if (namedCached.appendParameters() && ic.getParameters() != null && ic.getParameters().length > 0) {
				Object[] params = new Object[ic.getParameters().length + 1];
				System.arraycopy(ic.getParameters(), 0, params, 1, ic.getParameters().length);
				params[0] = namedCached.key();
				key = new CacheKey(params);
			} else {
				key = new CacheKey(new String[] { namedCached.key() });
			}
		}

		String name = namedCached.value();
		Cache<Object, Object> cache = cacheService.getCache(name);

		try {
			return cache.get(key);
		} catch (IllegalArgumentException e) {
			Object result = ic.proceed();
			cache.put(key, result);
			return result;
		}
	}
}
