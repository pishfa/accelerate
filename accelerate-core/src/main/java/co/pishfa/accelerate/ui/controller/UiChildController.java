package co.pishfa.accelerate.ui.controller;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that this controller should be treated as a child controller of another controller. Child controllers
 * receive ui events like their parents. Parents might also pass other info to them such as the parent object itself.
 * The parent controller of child controllers are automatically set by their parents.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface UiChildController {

}
