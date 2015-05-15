/**
 * 
 */
package co.pishfa.accelerate.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * A composition of several configs. For get operations, it linearly search through the available configs
 * until first finds a non-null value. For set operations, if it finds the entry in one of the configs sets it
 * there otherwise it sets the value into the first configuration in the list.
 *
 * @author Taha Ghasemi
 * 
 */
public class CompositeConfig extends AbstractConfig {

	private final List<Config> configs = new ArrayList<>();

	public CompositeConfig add(Config config) {
		configs.add(config);
		return this;
	}

	public CompositeConfig addFirst(Config config) {
		configs.add(0, config);
		return this;
	}

	public void remove(Config config) {
		configs.remove(config);
	}

	@Override
	public <T> T getObject(String key) {
		for (Config config : configs) {
			T value = config.getObject(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

    @Override
	public void setObject(String key, Object value) {
		for (Config config : configs) {
            Object oldValue = config.getObject(key);
			if (oldValue != null) {
				config.setObject(key, value);
				return;
			}
		}
		Validate.isTrue(!configs.isEmpty(), "At least one configuration should be available.");
		configs.get(0).setObject(key, value);
	}

}
