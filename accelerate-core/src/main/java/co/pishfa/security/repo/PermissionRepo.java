package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authorization.Permission;

import java.util.List;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PermissionRepo extends BaseJpaRepo<Permission, Long> {

	public static PermissionRepo getInstance() {
		return CdiUtils.getInstance(PermissionRepo.class);
	}

	public List<Permission> findByPrincipals(List<Long> principals) {
		return query(
				"select p from Permission p left join fetch p.definition left join fetch p.definition.action left join fetch p.definition.scope where p.principalId in (:pids) order by p.precedence asc")
				.with("pids", principals).list();
	}

	public void deleteByPrincipal(Principal principal) {
		query().delete().whereTrue().andEntityField("principalId = :principalId")
				.andEntityField("principalType = :principalType").with("principalId", principal.getId())
				.with("principalType", principal.getType()).run();
	}

	public List<Permission> findByPrincipal(Principal principal) {
		return getEntityManager()
				.createQuery(
						"select p from Permission p left join fetch p.definition where p.principalId = ?1 and p.principalType = ?2 order by p.precedence",
						Permission.class).setParameter(1, principal.getId()).setParameter(2, principal.getType())
				.getResultList();
	}

	@Override
	public Permission edit(Permission obj) {
		// find the previous one first, to detect change of scope, and then delete the params
		if (obj.getId() != null) {
			Permission old = findById(obj.getId());
			if (!old.getDefinition().getScope().equals(obj.getDefinition().getScope())) {
				PermissionParamRepo.getInstance().deleteByPermission(obj);
			}
		}
		return super.edit(obj);
	}

}
