package co.pishfa.accelerate.async;

import javax.interceptor.InvocationContext;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public interface AsyncStrategy {

    AsyncHandle run(InvocationContext ic) throws Exception;
}
