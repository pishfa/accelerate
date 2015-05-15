package co.pishfa.accelerate.config;

import co.pishfa.security.service.PersistentConfigEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * This class keep track of config changes, so later they can be persisted.
 * @author Taha Ghasemi
 */
public class TrackerConfig extends AbstractConfig {

    private Map<String, Object> changes = new HashMap<String, Object>();
    private final Config delegate;

    public TrackerConfig(Config delegate) {
        this.delegate = delegate;
    }

    public Map<String, Object> getAndEmptyChanges() {
        Map<String, Object> res = null;
        synchronized (changes) {
            res = changes;
            changes = new HashMap<String, Object>();
        }
        return res;
    }

    @Override
    public <T> T getObject(String key) {
        T changed = (T) changes.get(key);
        if(changed != null)
            return changed;
        return delegate.getObject(key);
    }

    @Override
    public void setObject(String key, Object value) {
        //we don't keep track changes of persistent objects, jpa do
        if(value instanceof PersistentConfigEntity) {
            synchronized (changes) {
                changes.put(key, value);
            }
        } else {
            Object old = getObject(key);
            //canonical representation is string, so convert everything to string.
            String v = value == null? null : value.toString();
            if (old == null || !old.toString().equals(v)) {
                synchronized (changes) {
                    changes.put(key, v);
                }
            }
        }
    }
}
