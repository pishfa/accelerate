/**
 *
 */
package co.pishfa.accelerate.config;

import java.util.List;
import java.util.Map;

/**
 * Represents a set of configurations entries. The entries may not be in the memory. This class
 * contains various getters to get the desired entry by its key casted to the given type. The canonical type of values are String. These methods return null if
 * the provide key is not available but throw exception if the type is not correct. This class is a map but only a limited number of operations is supported in the returned
 * map.
 *
 * @author Taha Ghasemi
 */
public interface Config extends Map<String, Object> {

    public String getString(String key);

    public Integer getInteger(String key);

    public Long getLong(String key);

    public Float getFloat(String key);

    public Boolean getBoolean(String key);

    /**
     * Retrieves the entry with the given key. Note that no type conversion is done.
     */
    public <T> T getObject(String key);

    /**
     * Retrieves the entry with the given key after converting to the specified type using the {@link co.pishfa.accelerate.convert.Converter} mechanism.
     * If the given type is annotated with {@link co.pishfa.accelerate.config.ConfigEntity} then this method tries to read its properties and then creates that object. In this case, if key is not specified, it use the key of entity itself.
     */
    public <T> T getObject(String key, Class<T> type);

    /**
     * Retrieves the configuration entry. The type should be annotated with {@link co.pishfa.accelerate.config.ConfigEntity}
     * and the key is retrieved from the alias of that config entity.
     */
    public <T> T getObject(Class<T> type);

    public <T extends Enum<T>> T getEnum(String key, Class<T> type);

    /**
     * Converts the value to String, and then use split with the separator char specified in the
     * ConfigConfig.
     */
    public <T> List<T> getList(String key, Class<T> type);

    /**
     * Sets the value of given key. The change may not be persisted until {@link co.pishfa.accelerate.config.cdi.ConfigService#edit(Config)} is called.
     * This method does not fire {@link co.pishfa.accelerate.core.ConfigAppliedEvent}.
     */
    public void setObject(String key, Object value);

    /**
     * Sets the configuration entry. The type should be annotated with {@link co.pishfa.accelerate.config.ConfigEntity}
     * and the key is retrieved from the alias of that config entity.
     */
    public void setObject(Object value);

}
