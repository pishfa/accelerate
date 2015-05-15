package co.pishfa.accelerate.async;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class ExecutorAsyncHandle implements AsyncHandle {
    private ScheduledFuture<?> future;
    private String key;
    private ExecutorAsyncStrategy strategy;

    public ExecutorAsyncHandle(ScheduledFuture<?> future, String key, ExecutorAsyncStrategy strategy) {
        this.future = future;
        this.key = key;
        this.strategy = strategy;
    }

    @Override
    public void cancel() {
        future.cancel(true);
        if(strategy != null)
            strategy.remove(key);
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public String getKey() {
        return key;
    }

    public ExecutorAsyncStrategy getStrategy() {
        return strategy;
    }
}
