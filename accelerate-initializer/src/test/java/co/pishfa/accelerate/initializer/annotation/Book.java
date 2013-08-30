package co.pishfa.accelerate.initializer.annotation;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public @interface Book {

	String title();

	Class<?> author() default Object.class;

	String edition() default "";

}
