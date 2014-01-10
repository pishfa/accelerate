/**
 * 
 */
package co.pishfa.accelerate.initializer.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.pishfa.accelerate.initializer.core.DefaultInitializer;

/**
 * Specifies the initialization aspects of this property to be used in {@link DefaultInitializer}. Can be used in types
 * that annotated with {@link InitEntity} or {@link Entity}.
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.FIELD })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface InitProperty {

	/**
	 * @return the name of this property in the class. If you place this annotation above the property itself this is
	 *         not required but if you put this above a class, you should set this too. It can also be the name of
	 *         special attributes: _anchor_,_in-parent_,_action_.
	 */
	public String name() default "";

	/**
	 * Default value for this property. If dynamic is set to true (default), can be an expression language (EL). In the
	 * EL context the following variables are available:
	 * <ul>
	 * <li>this (refers to the attributes of the current element in xml file + those with default values)</li>
	 * <li>parents (stack of parents, so parents.peek(1) is the first parent above the current element)</li>
	 * <li>anchors (map of anchors)</li>
	 * <li>entity (refers to the current ProcessEntity</li>
	 * </ul>
	 * The following shorthand notations could also be used:
	 * <ul>
	 * <li>@parent : means find the first appropriate parent that matches this property type</li>
	 * <li>@parent(i) : refers to the ith parent in the stack. i=0 means current object. i=1 means parent of this
	 * element an so on</li>
	 * <li>@anchor_name : refers to an object with specified anchor name, which should previously be defined. In case
	 * that anchor name is Alias:name, the Alias could also be eliminated if it could be automatically guessed from
	 * property type (this feature is called auto-scoping). If the target property type is a list, the following
	 * notation is also works \@anchor_name1;@anchor_name2;... But note that in this case auto-scoping is not work.</li>
	 * <li>(on of the above)? : makes the find procedure optional. For instance parent(1)? means set to parent(1) if
	 * exists otherwise set it to null</li>
	 * </ul>
	 * .
	 * <p>
	 * If the name of this property is _anchor_ and its value is "", then no anchor is computed for this entity.
	 * 
	 * @return
	 */
	public String value() default "";

	/**
	 * 
	 * @return an alias to be used in xml instead of this property original name
	 */
	public String alias() default "";

	/**
	 * 
	 * @return The value should be false, if the value should not be treated as an expression language (EL).
	 */
	public boolean dynamic() default true;

}
