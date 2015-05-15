/**
 * 
 */
package co.pishfa.accelerate.ui.phase;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import co.pishfa.accelerate.log.Logged;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Target({ METHOD })
@Retention(RUNTIME)
@Documented
@InterceptorBinding
@Logged
@Inherited
public @interface UiPhaseAction {

	PhaseId value();

	/**
	 * Should action be called on post backs too
	 * 
	 */
	boolean onPostback() default false;

	boolean after() default true;

}
