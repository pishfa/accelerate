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
 * Specifies custom conditions for query building. This should be placed on the parameters of a method that annotated
 * with {@link QueryRunner}.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {

	/**
	 * to set parameter by name instead of its position. Note that you can not mix named parameters with positional
	 * parameters so either all parameters must have a name or all must not be named.
	 * 
	 * @return the name of query parameter.
	 */
	String name() default "";

    /**
     * @return if true, this parameter will not be added as a query parameter
     */
    boolean ignore() default false;

    /**
     * @return if true, this parameter only set when its value is not null.
     */
    boolean optional() default false;

}
