/**
 * 
 */
package co.pishfa.security.service.handler;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.security.repo.UserRepo;
import co.pishfa.security.entity.authentication.Identity;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.entity.authorization.AccessLevel;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.PermissionDef;
import co.pishfa.security.entity.authorization.PermissionDefParam;
import co.pishfa.security.entity.authorization.SecuredEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@ScopeHandler("selfShared")
public class SelfSharedPermissionHandler implements PermissionScopeHandler<SecuredEntity> {

	@Override
	public boolean check(Identity identity, SecuredEntity target, String action, Permission permission)
			throws co.pishfa.security.exception.AuthorizationException {
		User createdBy = target.getCreatedBy();
		if (createdBy != null) {
			return (createdBy.equals(identity.getUser()) || createdBy.equals(UserRepo.getInstance().findGuest()))
					&& checkAccessLevel(permission.getDefinition(), target);
		} else {
			return false; // when object does created by anybody means it cannot
							// be accessed by the self rule
		}
	}

	@Override
	public void addConditions(Identity identity, Permission permission, QueryBuilder<SecuredEntity> query) {
		query.and("(e.createdBy.id = :createdBy or e.createdBy.id = :sharedUserId)");
		query.and("e.accessLevel >= :perm_req_level");
		query.with("createdBy", identity.getUser().getId());
		query.with("sharedUserId", UserRepo.getInstance().getSharedUserId());
		query.with("perm_req_level", computeRequiredLevel(permission.getDefinition()));
	}

	/**
	 * @param targetObj
	 * @return
	 */
	protected boolean checkAccessLevel(PermissionDef def, SecuredEntity targetObj) {
		if (targetObj.getAccessLevel() != null) {
			AccessLevel requiredLevel = computeRequiredLevel(def);
			return requiredLevel.getLevel() <= targetObj.getAccessLevel().getLevel();
		}
		return true;
	}

	private AccessLevel computeRequiredLevel(PermissionDef def) {
		PermissionDefParam param = def.getParam("requiredLevel");
		AccessLevel requiredLevel = AccessLevel.READ_WRITE; // by default
		if (param != null) {
			requiredLevel = AccessLevel.valueOf(param.getValue());
		}
		return requiredLevel;
	}

}
