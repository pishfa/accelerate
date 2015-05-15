/**
 * 
 */
package co.pishfa.accelerate.log;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Taha Ghasemi
 * 
 */
@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(LoggedInterceptor.class);

	public LoggedInterceptor() {
	}

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Logger logger;
		if (ic.getTarget() instanceof LoggerHolder) {
			logger = ((LoggerHolder) ic.getTarget()).getLogger();
		} else {
			logger = log;
		}
		if (logger.isDebugEnabled()) {
			long start = System.currentTimeMillis();
			logger.debug("Entering method " + ic.getMethod().getName());
			Object result = ic.proceed();
			logger.debug("Exiting method " + ic.getMethod().getName() + ". Time: "
					+ (System.currentTimeMillis() - start));
			return result;
		} else {
			return ic.proceed();
		}
	}

}
