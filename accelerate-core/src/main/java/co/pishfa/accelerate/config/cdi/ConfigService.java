/**
 *
 */
package co.pishfa.accelerate.config.cdi;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.*;
import co.pishfa.accelerate.config.entity.PersistentConfig;
import co.pishfa.accelerate.config.entity.PersistentConfigEntry;
import co.pishfa.accelerate.config.persistence.PersistentConfigEntityRepo;
import co.pishfa.accelerate.config.persistence.PersistentConfigEntryRepo;
import co.pishfa.accelerate.config.persistence.PersistentConfigRepo;
import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.resource.ResourceUtils;
import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.service.PersistentConfigEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: add monitoring of config changes
 *
 * @author Taha Ghasemi
 */
@Service
public class ConfigService {

    public static ConfigService getInstance() {
        return CdiUtils.getInstance(ConfigService.class);
    }

    @Inject
    private PersistentConfigRepo configRepository;

    @Inject
    private PersistentConfigEntryRepo configEntryRepository;

    @Inject
    private PersistentConfigEntityRepo configEntityRepository;

    private final Map<ConfigEntity, ConfigEntityData> configEntities = new HashMap<>();
    private final CompositeConfig compositeConfig = new CompositeConfig();
    private final TrackerConfig trackerConfig = new TrackerConfig(compositeConfig);
    private final ConfigEntityConfig config = new ConfigEntityConfig(trackerConfig, configEntities);

    @Inject
    private Event<ConfigAppliedEvent> configAppliedEvent;


    public void addEntityClass(Class<?> configEntityClass) {
        ConfigEntity configEntity = configEntityClass.getAnnotation(ConfigEntity.class);
        configEntities.put(configEntity, new ConfigEntityData(configEntity.value(), configEntityClass));
    }

    public void reloadConfiguration() throws Exception {
        compositeConfig.clear();
        loadConfiguration();
    }

    /**
     * @return false if system does not find a persistent config, which indicates that persistence is not initialized yet.
     */
    public boolean loadConfiguration() throws Exception {
        for (Class<?> entityClass : ConfigExtention.getAnnotatedEntities()) {
            addEntityClass(entityClass);
        }

        HashTableConfig hashTableConfig = new HashTableConfig();
        XmlConfigReader xmlConfig = new XmlConfigReader(hashTableConfig);
        for (URL url : ResourceUtils.getResources("config.default.xml")) {
            loadConfig(xmlConfig, url);
        }

        for (URL url : ResourceUtils.getResources("config.xml")) {
            loadConfig(xmlConfig, url);
        }

        boolean res = false;
        try {
            PersistentConfig persistentConfig = configRepository.findLatest();
            PersistentConfigReader persistentConfigReader = new PersistentConfigReader(hashTableConfig);
            persistentConfigReader.loadEntries(persistentConfig);
            persistentConfigReader.loadEntities(persistentConfig, configEntityRepository, configEntities);
            res = true;
        } catch (NoResultException e) {
        }

        compositeConfig.add(hashTableConfig);

        compositeConfig.add(new SystemConfig());

        return res;
    }

    private void loadConfig(final XmlConfigReader config, final URL url) throws IOException, Exception {
        try (InputStream stream = url.openStream()) {
            config.load(stream);
        }
    }

    /**
     * @return the current config
     */
    @Produces
    @Named
    @Global
    @ApplicationScoped
    public Config getConfig() {
        return config;
    }

    /**
     * Ignores the changes made to the config
     */
    public void reset() {
        trackerConfig.getAndEmptyChanges();
    }


    @Produces
    @ConfigStaticProperty("")
    public Object getConfigStaticProperty(final InjectionPoint injectionPoint) {
        String key = injectionPoint.getAnnotated().getAnnotation(ConfigStaticProperty.class).value();
        Class<?> fieldType = ((Field) injectionPoint.getMember()).getType();
        if(StrUtils.isEmpty(key)) {
            if(fieldType.isAnnotationPresent(ConfigEntity.class))
                return config.getObject(key, fieldType);
            key = injectionPoint.getMember().getName();
        }
        StringBuilder alias = new StringBuilder();
        appendEnclosingAlias(((Field) injectionPoint.getMember()).getDeclaringClass(), alias);
        return config.getObject(alias.append(key).toString(), fieldType);
    }

    /**
     * Makes the changed applied to the config, permanent by saving them to the database.
     */
    //TODO config is not used
    @Action("config.edit")
    public Config edit(Config config) {
        //TODO what if transaction fails
        Map<String, Object> changes = trackerConfig.getAndEmptyChanges();
        PersistentConfig persistentConfig = configRepository.findLatest();
        for (Map.Entry<String,Object> e : changes.entrySet()) {
            Object value = e.getValue();
            //make the changes persistent
            if(value != null && value instanceof PersistentConfigEntity) {
                value = configEntityRepository.save((PersistentConfigEntity) value);
            } else {
                PersistentConfigEntry entry = configEntryRepository.findByConfigAndName(persistentConfig, e.getKey());
                if (entry != null) {
                    if (value == null) {
                        configEntryRepository.delete(entry.getId());
                    } else {
                        entry.setValue(value.toString());
                        configEntryRepository.edit(entry);
                    }
                } else if (value != null) {
                    entry = new PersistentConfigEntry(e.getKey(), persistentConfig, value);
                    configEntryRepository.add(entry);
                }
            }
            //save the changes in memory
            compositeConfig.setObject(e.getKey(), value);
        }
        if(!changes.isEmpty())
            configAppliedEvent.fire(new ConfigAppliedEvent(config));
        return config;
    }

    public static void appendEnclosingAlias(Class<?> type, StringBuilder res) {
        if(type != null) {
            ConfigEntity configEntity = type.getAnnotation(ConfigEntity.class);
            if(configEntity!=null) {
                appendEnclosingAlias(type.getEnclosingClass(), res);
                res.append(configEntity.value()).append('.');
            }
        }
    }
}
