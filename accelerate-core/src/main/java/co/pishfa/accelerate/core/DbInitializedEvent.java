/**
 * 
 */
package co.pishfa.accelerate.core;

import co.pishfa.accelerate.common.AuditableEvent;
import co.pishfa.security.Audited;

/**
 * Fires when database is written for the first time.
 * 
 * @author Taha Ghasemi
 * 
 */
public class DbInitializedEvent implements AuditableEvent {

	@Override
	@Audited(action = "database.initialized")
	public void audit() {
	}

}
