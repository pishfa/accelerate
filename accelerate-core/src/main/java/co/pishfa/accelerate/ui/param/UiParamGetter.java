/**
 * 
 */
package co.pishfa.accelerate.ui.param;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

import co.pishfa.accelerate.validation.AutoValidating;

/**
 * Indicates that the return value of this getter method should be read from the corresponding request parameter.
 * 
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Documented
@InterceptorBinding
@AutoValidating
@Inherited
public @interface UiParamGetter {

	/**
	 * Parameter name. Default name is the method name with the getParam removed (if present). so either getParamId() or
	 * id() will return parameter with name id.
	 */
	@Nonbinding
	public String value() default "";

}
