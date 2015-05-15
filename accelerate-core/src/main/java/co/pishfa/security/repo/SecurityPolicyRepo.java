package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.SecurityPolicy;

/**
 * @author Taha Ghasemi
 * 
 */
@Repository
public class SecurityPolicyRepo extends BaseJpaRepo<SecurityPolicy, Long> {

}
