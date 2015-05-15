package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authorization.Role;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class RoleRepo extends BaseJpaRepo<Role, Long> {

	public static RoleRepo getInstance() {
		return CdiUtils.getInstance(RoleRepo.class);
	}

}
