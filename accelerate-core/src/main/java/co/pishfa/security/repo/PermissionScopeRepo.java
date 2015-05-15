package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authorization.PermissionScope;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PermissionScopeRepo extends BaseJpaRepo<PermissionScope, Long> {

	public static PermissionScopeRepo getInstance() {
		return CdiUtils.getInstance(PermissionScopeRepo.class);
	}

}
