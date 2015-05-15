/**
 * 
 */
package co.pishfa.accelerate.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import co.pishfa.accelerate.exception.UiExceptional;
import co.pishfa.accelerate.log.Logged;

/**
 * @author Taha Ghasemi
 * 
 */
@Logged
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@InterceptorBinding
@Inherited
@UiExceptional
public @interface UiAction {

}
