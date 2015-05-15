package co.pishfa.accelerate.convert;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Indicates a converter that do not throws exceptions and instead returns null.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface Unchecked {

}
