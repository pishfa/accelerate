/**
 * 
 */
package co.pishfa.accelerate.clone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set the field to null during cloning
 * 
 * @author Taha Ghasemi
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CloneNull {

}
