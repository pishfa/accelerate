package co.pishfa.accelerate.config;

import co.pishfa.accelerate.utility.CommonUtils;
import co.pishfa.accelerate.utility.StrUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * If the target class is annotated with {@link co.pishfa.accelerate.config.ConfigEntity} but it is not a persistent entity
 * (i.e. it does not extends {@link co.pishfa.security.service.PersistentConfigEntity}, this class tries to build one
 * from the available configuration items during getXXX. During setXXX this class converts the object back to configuration items.
 *
 * @author Taha Ghasemi
 */
public class ConfigEntityConfig extends AbstractConfig {

    private static final Logger log = LoggerFactory.getLogger(ConfigEntityConfig.class);

    private final Map<ConfigEntity, ConfigEntityData> configEntities;
    private final Config delegate;

    public ConfigEntityConfig(Config delegate, Map<ConfigEntity, ConfigEntityData> configEntities) {
        this.delegate = delegate;
        this.configEntities = configEntities;
    }

    @Override
    public <T> T getObject(String key) {
        return delegate.getObject(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> type) {
        ConfigEntity configEntity = type.getAnnotation(ConfigEntity.class);
        if (configEntity != null) {
            ConfigEntityData configEntityData = configEntities.get(configEntity);
            Validate.notNull(configEntityData);
            key = StrUtils.defaultIfEmpty(key, configEntityData.getAlias());
            if(!configEntityData.isPersistent()) {
                try {
                    Object entity = configEntityData.getType().newInstance();
                    for (ConfigEntityData.ConfigPropertyData property : configEntityData.getProperties()) {
                        Class<?> propType = PropertyUtils.getPropertyDescriptor(entity, property.getName()).getPropertyType();
                        Object value = super.getObject(key + "." + property.getAlias(), propType);
                        BeanUtils.setProperty(entity, property.getName(), value);
                    }
                    return CommonUtils.cast(entity);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }

        return super.getObject(key, type);
    }

    @Override
    public void setObject(String key, Object value) {
        if (value != null) {
            ConfigEntity configEntity = value.getClass().getAnnotation(ConfigEntity.class);
            if (configEntity != null) {
                ConfigEntityData configEntityData = configEntities.get(configEntity);
                Validate.notNull(configEntityData);
                key = StrUtils.defaultIfEmpty(key, configEntityData.getAlias());
                if(!configEntityData.isPersistent()) {
                    try {
                        for (ConfigEntityData.ConfigPropertyData property : configEntityData.getProperties()) {
                            Object propertyValue = PropertyUtils.getProperty(value, property.getName());
                            delegate.setObject(key + "." + property.getAlias(), propertyValue);
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    return;
                }
            }
        }
        delegate.setObject(key, value);
    }

}
