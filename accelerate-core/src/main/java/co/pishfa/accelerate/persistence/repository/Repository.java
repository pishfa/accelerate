/**
 * 
 */
package co.pishfa.accelerate.persistence.repository;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Named;

@Stereotype
@Named
@ApplicationScoped
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
/**
 * @author Taha Ghasemi
 *
 */
public @interface Repository {

}
