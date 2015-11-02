package co.pishfa.accelerate.ui.controller.entity;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A convenient class to set options of ui controllers. All options are considered as having type String so that default values
 * of ui controllers can be preserved.
 *
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface UiControllerOptions {

    String pageSize() default "";
    String add() default "";
    String edit() default "";
    String delete() default "";
    String multiSelect() default "";
    String local() default "";
    String id() default "";
    String outcome() default "";
    String viewAction() default "";
    String sortAscending() default "";
    String sortOn() default "";
    String secured() default "";
    String autoReload() default "";
    String preserveCurrent() default "";

}
