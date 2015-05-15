/**
 * 
 */
package co.pishfa.accelerate.service;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;

/**
 * @author Taha Ghasemi
 * 
 */
@Stereotype
@ApplicationScoped
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface Service {

}
