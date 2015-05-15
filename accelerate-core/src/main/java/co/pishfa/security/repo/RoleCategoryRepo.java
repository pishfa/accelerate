package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.hierarchical.BaseHierarchicalEntityJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authorization.RoleCategory;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class RoleCategoryRepo extends BaseHierarchicalEntityJpaRepo<RoleCategory, Long> {

}
