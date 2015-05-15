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

import co.pishfa.accelerate.validation.AutoValidating;

/**
 * This annotation can be used with fields or setters to inject the value of the corresponding parameter on view restore
 * phase. Note that for getter methods you should use {@link UiParamGetter} above them.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * @see {@link UiParamGetter}
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target({ ElementType.FIELD, ElementType.METHOD })
@AutoValidating
public @interface UiParam {

	/**
	 * Parameter name
	 */
	public String value() default "";

	/**
	 * If true, also inject the parameter value on postbacks
	 */
	public boolean onPostback() default false;

	/**
	 * If the parameter is not available should we inject null (true) or do nothing (false, which is default).
	 */
	public boolean nullIfMissed() default false;

}
