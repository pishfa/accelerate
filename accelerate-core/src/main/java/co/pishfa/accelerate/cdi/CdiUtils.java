/**
 * 
 */
package co.pishfa.accelerate.cdi;

import co.pishfa.accelerate.utility.CommonUtils;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.util.ProxyUtils;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Taha Ghasemi
 * 
 */
public class CdiUtils {

	public static BeanManager getBeanManager() {
		return BeanManagerProvider.getInstance().getBeanManager();
	}

	/**
	 * @return the bean manager associated to the servlet context.
	 */
	public static BeanManager getBeanManager(final ServletContext context) {
		return (BeanManager) context.getAttribute("javax.enterprise.inject.spi.BeanManager");
	}

	public static <T> T getInstance(final Class<T> type, final Annotation... qualifiers) {
		BeanManager manager = getBeanManager();
		return getInstance(manager, type, qualifiers);
	}

	public static <T> T getObject(final Class<T> type, final Annotation... qualifiers) {
		BeanManager manager = getBeanManager();
		return getObject(manager, type, qualifiers);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllInstances(final Class<T> type) {
		BeanManager manager = getBeanManager();
		Set<Bean<?>> beans = manager.getBeans(type, new AnyLiteral());
		List<T> result = new ArrayList<T>(beans.size());
		for (Bean<?> bean : beans) {
			result.add(getBeanReference(manager, (Bean<T>) bean, type));
		}
		return result;
	}

	public static <T> T getInstance(final BeanManager manager, Class<T> type, final Annotation... qualifiers) {
        type = CommonUtils.cast(ProxyUtils.getUnproxiedClass(type));
		Bean<T> bean = getBean(manager, type, qualifiers);
		return getBeanReference(manager, bean, type);
	}

	public static <T> T getObject(final BeanManager manager, final Class<T> type, final Annotation... qualifiers) {
		Bean<T> bean = getBean(manager, type, qualifiers);
		return getBeanObject(manager, bean, type);
	}

	@SuppressWarnings("unchecked")
	public static <T> Bean<T> getBean(final BeanManager manager, final Class<T> type, final Annotation... qualifiers) {
		return (Bean<T>) manager.resolve(manager.getBeans(type, qualifiers));
	}

	@SuppressWarnings("unchecked")
	public static <T> Bean<T> getExactBean(final BeanManager manager, final Class<T> type,
			final Annotation... qualifiers) {
		Set<Bean<?>> beans = manager.getBeans(type, qualifiers);
		if (beans.size() == 1) {
			return (Bean<T>) beans.iterator().next();
		}
		for (Bean<?> bean : beans) {
			if (bean.getBeanClass().equals(type)) {
				return (Bean<T>) bean;
			}
		}
		throw new AmbiguousResolutionException();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBeanReference(final BeanManager manager, Bean<T> bean, final Class<T> type) {
		T result = null;
		if (bean != null) {
			CreationalContext<T> context = manager.createCreationalContext(bean);
			if (context != null) {
				result = (T) manager.getReference(bean, type, context);
			}
		}
		return result;
	}

	/**
	 * @return original object without client proxy (but might be enhanced due to interceptors) of the bean.
	 */
	public static <T> T getBeanObject(final BeanManager manager, Bean<T> bean, final Class<T> type) {
		return manager.getContext(bean.getScope()).get(bean, manager.createCreationalContext(bean));
	}

	@SuppressWarnings("unchecked")
	public static CreationalContext<Object> inject(final Object instance) {
		if (instance != null) {
			BeanManager manager = getBeanManager();
			CreationalContext<Object> creationalContext = manager.createCreationalContext(null);
			InjectionTarget<Object> injectionTarget = (InjectionTarget<Object>) manager.createInjectionTarget(manager
					.createAnnotatedType(instance.getClass()));
			injectionTarget.inject(instance, creationalContext);
			return creationalContext;
		}
		return null;
	}

	public static <T> T putInContext(Class<T> type, Class<? extends Annotation> scope) {
		BeanManager manager = getBeanManager();
		Bean<T> bean = getBean(manager, type);
		Context context = manager.getContext(scope);
		return context.get(bean, manager.createCreationalContext(bean));
	}

}
