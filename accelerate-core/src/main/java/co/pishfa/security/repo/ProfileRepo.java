package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Profile;

/**
 * @author Taha Ghasemi
 * 
 */
@Repository
public class ProfileRepo extends BaseJpaRepo<Profile, Long> {

}
