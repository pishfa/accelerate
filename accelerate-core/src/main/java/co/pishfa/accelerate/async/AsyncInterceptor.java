/**
 *
 */
package co.pishfa.accelerate.async;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 */
@Async
@Interceptor
public class AsyncInterceptor implements Serializable {

    public static final String ASYNC_INVOCATION_CONTEXT = "co.pishfa.accelerate.async.AsynchronousInterceptor.invocationContext";

    private static final long serialVersionUID = 1L;

    // We need to track where the method is being invoked from so that we can
    // handle it properly.
    public static final String INVOKED_IN_THREAD = "INVOKED_IN_THREAD";
    public ThreadLocal<Boolean> invokedFromInterceptorInThread = new ThreadLocal<Boolean>();

    @Inject
    private AsyncStrategy strategy;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ic) throws Exception {
        if (invokedFromInterceptorInThread.get() == null) {
            if (ic.getContextData().get(INVOKED_IN_THREAD) == null) {
                return strategy.run(ic);
            } else {
                invokedFromInterceptorInThread.set(Boolean.TRUE);
                return ic.proceed();
            }
        } else {
            return ic.proceed();
        }
    }

}
