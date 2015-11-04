/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.exception.AuthorizationException;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.repo.PermissionParamRepo;

import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("custom")
public class CustomPermissionHandler implements PermissionScopeHandler<Entity<Long>> {

	@Override
	public boolean check(Identity identity, Entity<Long> target, String action, Permission permission)
			throws AuthorizationException {
		return PermissionParamRepo.getInstance().getCount(permission, target.getId()) > 0;
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<Entity<Long>> query) {
		List<Long> targetIds = PermissionParamRepo.getInstance().findTargetIds(permission);
		if (!targetIds.isEmpty()) {
			query.and("e.id in :entities_id").with("entities_id", targetIds);
		} else {
			query.and("1<>1");
		}
	}

}
