package co.pishfa.accelerate.initializer.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the target field is a key field of corresponding {@link InitEntity}. Multiple fields in an entity can be
 * annotated with this annotation an the result key is the comma-separated names of them.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Target({ ElementType.FIELD })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InitKey {

}
