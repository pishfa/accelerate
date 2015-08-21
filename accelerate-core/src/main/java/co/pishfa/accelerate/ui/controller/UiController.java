/**
 * 
 */
package co.pishfa.accelerate.ui.controller;

import org.omnifaces.cdi.ViewScoped;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.Nonbinding;
import javax.inject.Named;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the target class is a controller for a view. Multiple controllers can be defined for a single view, but only one of them must be defined as primary.
 * UiControllers, automatically received ui phase events for that view such as render.
 * @author Taha Ghasemi
 * 
 */
@Stereotype
@ViewScoped
@Named
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface UiController {

	/**
	 * The viewId or page name starting with ac: which this controller is its main controller. The default value is the
	 * class name with the first letter lower cases and appended with ac:
	 */
	@Nonbinding
	String value() default "";

	/**
	 * @return is this controller is the primary page controller.
	 */
	@Nonbinding
	boolean primary() default true;

}
