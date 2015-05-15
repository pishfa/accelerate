/**
 * 
 */
package co.pishfa.accelerate.ui;

import co.pishfa.accelerate.ui.controller.UiController;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
public class UiExtention implements Extension {

	private static List<Class<?>> controllers = new ArrayList<>();

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
		UiController uiController = javaClass.getAnnotation(UiController.class);
		if (uiController != null) {
			controllers.add(javaClass);
		}
	}

	/**
	 * @return the annotatedEntities
	 */
	public static List<Class<?>> getControllers() {
		return controllers;
	}

}
