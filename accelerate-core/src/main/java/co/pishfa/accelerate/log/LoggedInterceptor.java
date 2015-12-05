/**
 * 
 */
package co.pishfa.accelerate.log;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

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

		try {
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
		} catch (ConstraintViolationException e) {
			StringBuilder message = new StringBuilder("ConstraintViolationException: ");
			for(ConstraintViolation v : e.getConstraintViolations()) {
				message.append(v.getRootBeanClass()).append(".").append(v.getPropertyPath())
						.append("(").append(v.getRootBean()).append(".").append(v.getLeafBean()).append("<-").append(v.getInvalidValue()).append(")")
						.append(" ").append(v.getMessage())
						.append("\n");
			}
			logger.error(message.toString());
			throw e;
		}
	}

}
