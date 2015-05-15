/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;

/**
 * @author Taha Ghasemi
 * 
 */
public interface ActionHandler {

	public abstract boolean check(Identity identity, Object target, String action) throws AuthorizationException;

}
