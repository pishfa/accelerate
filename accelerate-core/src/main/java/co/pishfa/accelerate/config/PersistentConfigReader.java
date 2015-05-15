/**
 * 
 */
package co.pishfa.accelerate.config;

import co.pishfa.accelerate.config.persistence.PersistentConfigEntityRepo;
import co.pishfa.accelerate.config.entity.PersistentConfig;
import co.pishfa.accelerate.config.entity.PersistentConfigEntry;
import co.pishfa.accelerate.utility.StrUtils;

import java.util.Map;

/**
 * @author Taha Ghasemi
 * 
 */
public class PersistentConfigReader {

    private Config delegate;

    public PersistentConfigReader(Config delegate) {
        this.delegate = delegate;
    }

    public void loadEntries(PersistentConfig config) {
		for (PersistentConfigEntry entry : config.getEntries()) {
            delegate.setObject(entry.getName(), entry.getValue());
		}
	}

    public void loadEntities(PersistentConfig config, PersistentConfigEntityRepo repository, Map<ConfigEntity, ConfigEntityData> configEntities) {
        for(ConfigEntityData configEntity : configEntities.values()) {
            if(configEntity.isPersistent() && !StrUtils.isEmpty(configEntity.getAlias()))
                delegate.setObject(configEntity.getAlias(), repository.findLatestByName((Class) configEntity.getType(), configEntity.getAlias(), config));
        }
    }

}
