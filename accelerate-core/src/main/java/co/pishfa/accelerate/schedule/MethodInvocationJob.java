/**
 * 
 */
package co.pishfa.accelerate.schedule;

import co.pishfa.accelerate.async.AsyncInterceptor;
import co.pishfa.accelerate.cdi.CdiUtils;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.interceptor.InvocationContext;

/**
 * @author Taha Ghasemi
 * 
 */
public class MethodInvocationJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		InvocationContext ic = (InvocationContext) context.getJobDetail().getJobDataMap()
				.get(AsyncInterceptor.ASYNC_INVOCATION_CONTEXT);
		ContextControl contextControl = CdiUtils.getInstance(ContextControl.class);
		try {
            ic.getContextData().put(AsyncInterceptor.INVOKED_IN_THREAD, Boolean.TRUE);
            contextControl.startContext(SessionScoped.class);
            contextControl.startContext(RequestScoped.class);
			ic.proceed();
		} catch (Exception e) {
			throw new JobExecutionException(e);
		} finally {
			contextControl.stopContext(RequestScoped.class);
            contextControl.stopContext(SessionScoped.class);
		}
	}

}
