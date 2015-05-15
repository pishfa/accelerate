package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.audit.Audit;

import java.util.Date;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class AuditRepo extends BaseJpaRepo<Audit, Long> {

	@QueryRunner("delete from Audit where creationDate < ?1")
	public void deleteOlderThan(Date time) {
	}

}