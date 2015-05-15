/**
 * 
 */
package co.pishfa.accelerate.common;

import co.pishfa.security.service.AuditService;

/**
 * Represents an event that should be audited when fired.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface AuditableEvent {

	/**
	 * Audits this event using {@link AuditService}.
	 */
	void audit();

}
