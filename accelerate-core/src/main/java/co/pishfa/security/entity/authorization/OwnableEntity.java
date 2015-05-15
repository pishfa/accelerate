/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.security.entity.authentication.User;

/**
 * @author Taha Ghasemi
 *
 */
public interface OwnableEntity {
	
	public User getOwner();

}
