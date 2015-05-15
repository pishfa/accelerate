/**
 * 
 */
package co.pishfa.accelerate.config.cdi;

import co.pishfa.accelerate.config.ConfigEntity;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
public class ConfigExtention implements Extension {

	private static List<Class<?>> annotatedEntities = new ArrayList<>();


	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		if (javaClass.isAnnotationPresent(ConfigEntity.class)) {
            annotatedEntities.add(javaClass);
        }
	}

	/**
	 * @return the annotatedEntities
	 */
	public static List<Class<?>> getAnnotatedEntities() {
		return annotatedEntities;
	}

    /*
	 * public <I> void processInjectionTarget(@Observes final ProcessInjectionTarget<I> pit, final BeanManager beanManager) {
	 * final InjectionTarget<I> it = pit.getInjectionTarget();
	 * AnnotatedType<I> at = pit.getAnnotatedType();
	 * 
	 * }
	 */

}
