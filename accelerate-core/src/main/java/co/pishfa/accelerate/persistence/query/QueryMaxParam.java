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
 * Indicates the maximum number of results should be returned.
 * This should be placed on at most one of the parameters of a method that annotated with {@link QueryRunner}.
 * The type of the parameter should be int or {@link Integer}. If the {@link QueryRunner#maxResults()} is set, this parameter takes
 * precedence.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryMaxParam {

}
