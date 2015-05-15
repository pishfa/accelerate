package co.pishfa.accelerate.portal.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.accelerate.portal.entity.Page;

/**
 * The repository for {@link Page} entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PageRepo extends BaseJpaRepo<Page, Long> {

	public static PageRepo getInstance() {
		return CdiUtils.getInstance(PageRepo.class);
	}

}
