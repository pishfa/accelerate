package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authorization.Representative;

/**
 * @author Taha Ghasemi
 * 
 */
@Repository
public class RepresentativeRepo extends BaseJpaRepo<Representative, Long> {

	public static RepresentativeRepo getInstance() {
		return CdiUtils.getInstance(RepresentativeRepo.class);
	}

}
