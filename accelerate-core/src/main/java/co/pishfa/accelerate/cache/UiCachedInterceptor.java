package co.pishfa.accelerate.cache;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Interceptor
@UiCached
public class UiCachedInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private CacheService cacheService;

	@AroundInvoke
	public Object invoke(InvocationContext ic) throws Exception {
		UiCached cached = ic.getMethod().getAnnotation(UiCached.class);

		StringBuilder keyBuilder = new StringBuilder();

		if (StrUtils.isEmpty(cached.key())) {
			keyBuilder.append(ic.getTarget().getClass().getName()).append(".").append(ic.getMethod().getName());
		} else {
			keyBuilder.append(cached.key());
		}
		if (cached.appendParameters() && ic.getParameters() != null && ic.getParameters().length > 0) {
			for (Object param : ic.getParameters()) {
				keyBuilder.append("_");
				if (param instanceof Entity) {
					keyBuilder.append(((Entity) param).getId());
				} else
					keyBuilder.append(param);
			}
		}

		Cache<String, Object> cache = cacheService.getCache(cached.value());
		String key = keyBuilder.toString();
		Object result = cache.getIfPresent(key);
		if (result == null) {
			result = ic.proceed();
			if (result != null) {
				cache.put(key, result);
			}
		}
		return result;
	}
}
