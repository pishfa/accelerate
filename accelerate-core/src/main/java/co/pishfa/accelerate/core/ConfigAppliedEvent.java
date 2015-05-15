/**
 * 
 */
package co.pishfa.accelerate.core;

import co.pishfa.accelerate.common.AuditableEvent;
import co.pishfa.accelerate.config.Config;
import co.pishfa.security.Audited;

/**
 * Fired whenever a change in configurations happens. Note: this event may be fired when {@link FrameworkStartedEvent}
 * is not yet fired.
 *
 * TODO: this event should be propagated across the cluster.
 * 
 * @author Taha Ghasemi
 * 
 */
public class ConfigAppliedEvent implements AuditableEvent {

	private final Config config;

	public ConfigAppliedEvent(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	@Override
	@Audited(action = "config.applied")
	public void audit() {
	}

}
