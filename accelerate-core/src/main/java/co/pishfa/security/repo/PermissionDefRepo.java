package co.pishfa.security.repo;

import java.util.List;

import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.filter.SimpleFilter;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authorization.Action;
import co.pishfa.security.entity.authorization.PermissionDef;
import co.pishfa.security.entity.authorization.PermissionScope;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PermissionDefRepo extends BaseJpaRepo<PermissionDef, Long> {

	@QueryRunner
	public PermissionDef findByActionAndScope(String actionName, String scopeName) {
		return null;
	}

	@QueryRunner
	public PermissionDef findByActionAndScopeTitle(String actionName, String scopeName) {
		return null;
	}

	/**
	 * @param action
	 * @return
	 */
	public List<PermissionDef> findByActionSecured(final Action action) {
		Filter<PermissionDef> filter = new SimpleFilter<PermissionDef>("assign.permissionDef") {

			@Override
			public void addConditions(QueryBuilder<PermissionDef> query) {
				super.addConditions(query);
				query.append(" and action.id = :aid ").with("aid", action.getId());
			}
		};
		return find(filter);
	}

	/**
	 * @param parent
	 * @return
	 */
	@QueryRunner("from PermissionDef where representative = ?1")
	public PermissionDef findByRepresentative(Principal representative) {
		return null;
	}

	/**
	 * @param res
	 */
	public List<PermissionDef> findByIds(List<Long> permissionParamsId) {
		return query().select().where("id in (:ppid) order by id").with("ppid", permissionParamsId).list();
	}

	@Override
	public void delete(PermissionDef obj) {
		super.delete(obj);
		// We should delete those PermissionParams that point to it
		PermissionParamRepo.getInstance().deleteByTarget(obj);
	}

	@QueryRunner(where = "(?1 member of e.action.parents) and e.scope = ?2")
	public List<PermissionDef> findDescentsByActionAndScope(Action action, PermissionScope scope) {
		return null;
	}

}
