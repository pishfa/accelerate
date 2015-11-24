package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authorization.RoleAssignment;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import java.util.List;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class RoleAssignmentRepo extends BaseJpaRepo<RoleAssignment, Long> {

	public static RoleAssignmentRepo getInstance() {
		return CdiUtils.getInstance(RoleAssignmentRepo.class);
	}

	@SuppressWarnings("unchecked")
	public List<RoleAssignment> findByPrincipal(Principal principal) {
		return getEntityManager()
				.createQuery("select e from RoleAssignment e where e.principalId = ?1 and e.principalType = ?2")
				.setParameter(1, principal.getId()).setParameter(2, principal.getMetadata()).getResultList();
	}

	@Transactional
	public void deleteByPrincipal(Principal principal) {
		getEntityManager().createQuery("delete from RoleAssignment e where e.principalId = ?1 and e.principalType = ?2")
				.setParameter(1, principal.getId()).setParameter(2, principal.getType()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<RoleAssignment> findByPrincipals(List<Long> principalsId) {
		// TODO principalType is not taken into account
		return getEntityManager().createQuery("select e from RoleAssignment e where e.principalId in :pids")
				.setParameter("pids", principalsId).getResultList();
	}

}
