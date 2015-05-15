package co.pishfa.accelerate.persistence.filter;

import co.pishfa.accelerate.convert.ObjectConverter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Specifies that this field will be used in the filtering process. If the target type is instance of {@link List} its
 * values are ORed together. If the target type is of type {@link FilterInterval} then the default condition is
 * FilterFieldCondition.BETWEEN. In this case if either side is null, it will be handled as one ended interval
 * condition.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FilterField {

	FilterFieldCondition condition() default FilterFieldCondition.EQUALS;

	/**
	 * @return the left-side of expression to be added to the query. The entity alias is "e". Defaults to the e.field
	 *         name.
	 */
	String expr() default "";

    Class<? extends ObjectConverter> converter() default ObjectConverter.class;

    /**
     * @return true if this field can be auto cleaned. defaults to true for non-primitive types but primitive types are
     * not cleanable.
     */
    boolean cleanable() default true;
}
