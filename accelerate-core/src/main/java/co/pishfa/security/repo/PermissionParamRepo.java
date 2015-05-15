package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.PermissionParam;

import java.util.List;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PermissionParamRepo extends BaseJpaRepo<PermissionParam, Long> {

	public static PermissionParamRepo getInstance() {
		return CdiUtils.getInstance(PermissionParamRepo.class);
	}


	@QueryRunner("select count(pp) from PermissionParam pp where permission = ?1 and targetId = ?2")
	public long getCount(Permission permission, Long id) {
		return 0;
	}

	@QueryRunner("select pp.targetId from PermissionParam pp where permission = ?1")
	public List<Long> findTargetIds(Permission permission) {
		return null;
	}


	@QueryRunner(where = "permission = ? order by id")
	public List<PermissionParam> findByPermission(Permission permission) {
		return null;
	}


	@QueryRunner("delete from PermissionParam where permission = ?")
	public void deleteByPermission(Permission entity) {
	}

	public void deleteByTarget(Entity obj) {
		query().delete().where("targetId = :targetId and target = :target").with("targetId", obj.getId())
				.with("target", obj.getClass().getSimpleName()).run();
	}

}
