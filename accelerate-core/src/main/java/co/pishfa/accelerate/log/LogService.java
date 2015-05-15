/**
 * 
 */
package co.pishfa.accelerate.log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Taha Ghasemi
 * 
 */
@ApplicationScoped
public class LogService {

	private static final Logger log = LoggerFactory.getLogger(LogService.class);

	@Produces
	@Dependent
	public Logger getLogger(InjectionPoint injectionPoint) {
		if (injectionPoint != null && injectionPoint.getBean() != null)
			return LoggerFactory.getLogger(injectionPoint.getBean().getBeanClass().getName());
		return log;
	}
}
