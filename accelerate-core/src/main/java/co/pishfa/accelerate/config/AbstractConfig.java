/**
 *
 */
package co.pishfa.accelerate.config;

import co.pishfa.accelerate.convert.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Performs the casting process. Extensions only need to implement the {@link Config#getObject(String)} and
 * {@link Config#setObject(String, Object)} methods.
 *
 * @author Taha Ghasemi
 */
public abstract class AbstractConfig implements Config {

    private final ConfigConfig configConfig = ConfigConfig.getInstance();

    protected Converter convert() {
        return configConfig.getConverter();
    }

    @Override
    public String getString(String key) {
        Object value = getObject(key);
        return convert().toString(value);
    }

    @Override
    public Integer getInteger(String key) {
        Object value = getObject(key);
        return convert().toInteger(value);
    }

    @Override
    public Long getLong(String key) {
        Object value = getObject(key);
        return convert().toLong(value);
    }

    @Override
    public Float getFloat(String key) {
        Object value = getObject(key);
        return convert().toFloat(value);
    }

    @Override
    public Boolean getBoolean(String key) {
        Object value = getObject(key);
        return convert().toBoolean(value);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        Object value = getObject(key);
        return convert().toObject(value, type);
    }

    @Override
    public <T> T getObject(Class<T> type) {
        return getObject(null, type);
    }

    @Override
    public void setObject(Object value) {
        setObject(null, value);
    }

    @Override
    public <T extends Enum<T>> T getEnum(String key, Class<T> type) {
        Object value = getObject(key);
        return convert().toEnum(value, type);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> type) {
        String value = getString(key);
        String[] values = StringUtils.split(value, configConfig.getSeparatorChar());
        return convert().toList(values, type);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return getObject(String.valueOf(key)) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        return getObject(String.valueOf(key));
    }

    @Override
    public Object put(String key, Object value) {
        setObject(key, value);
        return value;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for (Entry<?, ?> entry : m.entrySet()) {
            setObject(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void clear() {
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Object> values() {
        return null;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return null;
    }

}
