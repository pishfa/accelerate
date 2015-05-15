/**
 *
 */
package co.pishfa.accelerate.config;

import co.pishfa.accelerate.config.cdi.ConfigService;
import co.pishfa.security.service.PersistentConfigEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Hold information about a custom configuration entity class.
 *
 * @author Taha Ghasemi
 */
public class ConfigEntityData {

    public static class ConfigPropertyData {
        private String name;
        private String alias;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }
    }

    private String alias;
    private Class<?> type;
    private List<ConfigPropertyData> properties;
    private boolean persistent;

    public ConfigEntityData(String alias, Class<?> type) {
        StringBuilder aliasBuilder = new StringBuilder();
        ConfigService.appendEnclosingAlias(type.getEnclosingClass(), aliasBuilder);
        this.alias =  aliasBuilder.append(alias).toString();
        this.type = type;
        if(!PersistentConfigEntity.class.isAssignableFrom(type)) {
            persistent = false;
            properties = new ArrayList<>();
            for (Field field : type.getDeclaredFields()) {
                ConfigPropertyData property = new ConfigPropertyData();
                property.setName(field.getName());
                ConfigProperty configProperty = field.getAnnotation(ConfigProperty.class);
                if (configProperty != null) {
                    if (!configProperty.ignore()) {
                        property.setAlias(configProperty.value());
                        properties.add(property);
                    }
                } else {
                    property.setAlias(field.getName());
                    properties.add(property);
                }
            }
        } else {
            persistent = true;
        }
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public List<ConfigPropertyData> getProperties() {
        return properties;
    }

    public boolean isPersistent() {
        return persistent;
    }
}
