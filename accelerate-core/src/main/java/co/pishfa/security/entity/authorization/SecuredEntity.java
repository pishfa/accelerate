/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.User;

/**
 * An entity with security related properties so the security system can protect it from unwanted accesses.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface SecuredEntity<K> extends Entity<K> {

	public abstract Domain getDomain();

	public abstract void setDomain(Domain secDomain);

	public abstract SecurityLevel getSecurityLevel();

	public abstract AccessLevel getAccessLevel();

	public abstract User getCreatedBy();

	public abstract void setCreatedBy(User user);

}