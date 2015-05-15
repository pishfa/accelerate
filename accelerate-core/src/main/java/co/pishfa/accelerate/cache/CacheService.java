/**
 * 
 */
package co.pishfa.accelerate.cache;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import net.sf.ehcache.CacheManager;
import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.ui.UiService;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class CacheService {

	@Inject
	private UiService uiService;

	public CacheManager getCacheManager() {
		return CacheManager.newInstance();
	}

	@SuppressWarnings("rawtypes")
	@Produces
	@NamedCache
	@Dependent
	public Cache getNamedCache(InjectionPoint injectionPoint) {
		String name = injectionPoint.getAnnotated()
				.getAnnotation(NamedCache.class).value();
		name = StrUtils.defaultIfEmpty(name, injectionPoint.getMember()
				.getName());
		return getCache(name);
	}

	@Produces
	@ContextCache(Annotation.class)
	@Dependent
	public Cache<Object, Object> getContextCache(InjectionPoint injectionPoint) {
		Class<? extends Annotation> scope = injectionPoint.getAnnotated()
				.getAnnotation(ContextCache.class).value();
		return getCache(scope);
	}

	@Produces
	@UiCache
	@Dependent
	public Cache<String, Object> getUiCache(InjectionPoint injectionPoint) {
		UiScope scope = injectionPoint.getAnnotated()
				.getAnnotation(UiCache.class).value();
		return getCache(scope);
	}

	/**
	 * @return a global cache with the given name
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <K, V> Cache<K, V> getCache(String name) {
		return new EhCacheWrapper(getCacheManager().getCache(name));
	}

	/**
	 * @return the cache associated with the given scope.
	 */
	@SuppressWarnings({ "unchecked" })
	public Cache<Object, Object> getCache(Class<? extends Annotation> scope) {
		return CdiUtils.putInContext(ConcurrentCache.class, scope);
	}

	/**
	 * @return the cache representation of the given ui scope. Only works in jsf
	 *         requests. In async methods these not work right now.
	 */
	public Cache<String, Object> getCache(UiScope scope) {
		switch (scope) {
		case REQUEST:
			return new ServletRequestCache(uiService.getRequest());
		case VIEW:
			return new MapWithNullCache<String, Object>(uiService.getView());
		case SESSION:
			return new HttpSessionCache(uiService.getSession());
		case APPLICATION:
			return new ServletContextCache(uiService.getServletContext());
		}
		return null;
	}
}
