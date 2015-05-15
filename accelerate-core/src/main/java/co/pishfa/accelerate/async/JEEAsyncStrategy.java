package co.pishfa.accelerate.async;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@ApplicationScoped
@Alternative
public class JEEAsyncStrategy extends ExecutorAsyncStrategy {

    @Resource
    private ManagedScheduledExecutorService managedScheduledExecutorService;

    @Override
    protected ScheduledExecutorService getExecutorService() {
        return managedScheduledExecutorService;
    }

}
