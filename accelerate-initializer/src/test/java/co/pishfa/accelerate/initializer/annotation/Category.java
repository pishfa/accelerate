package co.pishfa.accelerate.initializer.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import co.pishfa.accelerate.initializer.model.InitAnnotation;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@InitAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {

	String name() default "@type.name";

}
