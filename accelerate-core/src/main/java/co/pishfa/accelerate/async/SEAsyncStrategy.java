package co.pishfa.accelerate.async;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@ApplicationScoped
public class SEAsyncStrategy extends ExecutorAsyncStrategy {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    @Override
    protected ScheduledExecutorService getExecutorService() {
        return scheduledExecutorService;
    }

}
