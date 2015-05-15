/**
 * 
 */
package co.pishfa.accelerate.log;

import org.slf4j.Logger;

/**
 * Can be used in classes with @Logged annotation. In this case, the logged interceptor uses the provided Logger by this
 * class instead of its own class to do the logging.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface LoggerHolder {

	public Logger getLogger();

}
