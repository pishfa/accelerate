/**
 * 
 */
package co.pishfa.accelerate.persistence.query;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the first index where results should be returned from it. This should be placed on at most one of the
 * parameters of a method that annotated with {@link QueryRunner}. The type of the parameter should be int or
 * {@link Integer}.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryFirstParam {

}
