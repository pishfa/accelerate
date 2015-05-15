package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.hierarchical.BaseParentsHierarchicalEntityRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authorization.Action;

/**
 * The repository for {@link Action} entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class ActionRepo extends BaseParentsHierarchicalEntityRepo<Action> {

	public static ActionRepo getInstance() {
		return CdiUtils.getInstance(ActionRepo.class);
	}

}
