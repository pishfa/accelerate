package co.pishfa.accelerate.portal.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.accelerate.portal.entity.Site;

/**
 * The repository for {@link Site} entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class SiteRepo extends BaseJpaRepo<Site, Long> {

	public static SiteRepo getInstance() {
		return CdiUtils.getInstance(SiteRepo.class);
	}

}
