/**
 * 
 */
package co.pishfa.accelerate.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.faces.validator.ValidatorException;
import javax.interceptor.InterceptorBinding;

import co.pishfa.accelerate.message.UserMessage;

/**
 * Can be used on faces validator methods. It converts results of types String, {@link UserMessage}, or
 * {@link ValidationException} to the corresponding {@link ValidatorException} understandable by faces.
 * 
 * @author Taha Ghasemi
 * 
 */
@InterceptorBinding
@Inherited
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
public @interface UiValidator {

}
