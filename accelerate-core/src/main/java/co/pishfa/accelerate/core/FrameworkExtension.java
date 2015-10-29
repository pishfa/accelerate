/**
 * 
 */
package co.pishfa.accelerate.core;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.cdi.Veto;
import co.pishfa.accelerate.context.ThreadContext;
import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.security.service.handler.ScopeHandler;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * A CDI extension that finds entities annotated with {@link Entity} or {@link InitEntity}. It also adds the ignoring of
 * entities or packages annotated with {@link Veto}.
 * 
 * @author Taha Ghasemi
 * 
 */
public class FrameworkExtension implements Extension {

	private static List<Class<?>> annotatedEntities = new ArrayList<>();
	private static List<Class<?>> scopeHandlers = new ArrayList<>();

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
		final Class<T> javaClass = annotatedType.getJavaClass();
		final Package pkg = javaClass.getPackage();

		if (javaClass.isAnnotationPresent(Entity.class) || javaClass.isAnnotationPresent(InitEntity.class)) {
			annotatedEntities.add(javaClass);
		} else if (javaClass.isAnnotationPresent(ScopeHandler.class)) {
			scopeHandlers.add(javaClass);
		}

		if (javaClass.isAnnotationPresent(Veto.class) || (pkg != null && pkg.isAnnotationPresent(Veto.class))) {
			pat.veto();
			return;
		}

	}


	/**
	 * @return the annotatedEntities
	 */
	public static List<Class<?>> getAnnotatedEntities() {
		return annotatedEntities;
	}

	public static List<Class<?>> getScopeHandlers() {
		return scopeHandlers;
	}

	public void afterBeanDiscovery(@Observes AfterBeanDiscovery event) throws Exception {
		event.addContext(new ThreadContext());
	}

    public void afterDeploymentValidation(
            final @Observes AfterDeploymentValidation event,
            final BeanManager beanManager) {
		CdiUtils.getInstance(beanManager, FrameworkService.class).start();
	}

}
