/**
 *
 */
package co.pishfa.accelerate.async;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.schedule.ScheduleDelay;
import co.pishfa.accelerate.schedule.ScheduleInterval;
import co.pishfa.accelerate.schedule.ScheduleStart;
import org.apache.deltaspike.cdise.api.ContextControl;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.interceptor.InvocationContext;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Taha Ghasemi
 */
public abstract class ExecutorAsyncStrategy implements AsyncStrategy {

    public static class AsyncRunnable implements Runnable {

        private InvocationContext ic;

        public AsyncRunnable(InvocationContext ic) {
            this.ic = ic;
        }

        @Override
        public void run() {
            ContextControl contextControl = CdiUtils.getInstance(ContextControl.class);
            try {
                ic.getContextData().put(AsyncInterceptor.INVOKED_IN_THREAD, Boolean.TRUE);
                contextControl.startContext(SessionScoped.class);
                contextControl.startContext(RequestScoped.class);
                ic.proceed();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                contextControl.stopContext(RequestScoped.class);
                contextControl.stopContext(SessionScoped.class);
            }
        }
    }

    private Map<String, AsyncHandle> handles = new HashMap<>();

    public AsyncHandle run(InvocationContext ic)  {
        Async async = ic.getMethod().getAnnotation(Async.class);
        long delay = async.delay();
        long interval = async.interval();
        if (ic.getParameters() != null && ic.getParameters().length > 0) {
            int paramIndex = 0;
            Annotation[][] parametersAnnotations = ic.getMethod().getParameterAnnotations();
            for (Object parameter : ic.getParameters()) {
                Annotation[] parameterAnnotations = parametersAnnotations[paramIndex];
                if (parameterAnnotations.length > 0) {
                    if (parameterAnnotations[0] instanceof ScheduleStart) {
                        if (parameter instanceof Date)
                            delay = ((Date) parameter).getTime() - System.currentTimeMillis();
                        else
                            delay = ((long) parameter) - System.currentTimeMillis();
                        break;
                    } else if (parameterAnnotations[0] instanceof ScheduleDelay) {
                        delay =  (long) parameter;
                        break;
                    } else if (parameterAnnotations[0] instanceof ScheduleInterval) {
                        delay = (long) parameter;
                        break;
                    }
                }
                paramIndex++;
            }
        }
        Runnable run = new AsyncRunnable(ic);
        AsyncHandle handle = null;
        if(interval > 0) {
            handle = new ExecutorAsyncHandle(getExecutorService().scheduleAtFixedRate(run, delay, interval, TimeUnit.MILLISECONDS), null, null);
        } else {
            String key = ic.getTarget().getClass().getName() + "#" + ic.getMethod().getName();
            synchronized (handles) {
                if (async.reschedule() == RescheduleType.DELETE_PREV)
                    handles.remove(key);
                else if (async.reschedule() == RescheduleType.SKIP) {
                    handle = handles.get(key);
                    if (handle != null)
                        return handle;
                }
            }
            handle = new ExecutorAsyncHandle(getExecutorService().schedule(run, delay, TimeUnit.MILLISECONDS), key, this);
            synchronized (handles) {
                handles.put(key, handle);
            }
        }
        if (AsyncHandle.class.isAssignableFrom(ic.getMethod().getReturnType())) {
            return handle;
        } else
            return null;
    }

    protected abstract ScheduledExecutorService getExecutorService();

    public void remove(String key) {
        synchronized (handles) {
            handles.remove(key);
        }
    }

}
