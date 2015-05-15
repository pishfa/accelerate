/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;

/**
 * @author Taha Ghasemi
 * 
 */
public interface PermissionScopeHandler<T> {

	boolean check(Identity identity, T target, String action, Permission permission) throws AuthorizationException;

	/**
	 * Adds the required conditions to the where part of the query to filter out those entities that are not allowed to
	 * be seen according to the provided viewPermission and the scope of this handler.
	 */
	void addConditions(Identity identity, Permission permission, QueryBuilder<T> query);

}
