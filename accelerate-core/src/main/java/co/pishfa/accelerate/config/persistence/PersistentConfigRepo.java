package co.pishfa.accelerate.config.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.entity.PersistentConfig;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;

/**
 * The repository for {@link co.pishfa.accelerate.config.entity.PersistentConfig} entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PersistentConfigRepo extends BaseJpaRepo<PersistentConfig, Long> {

	public static PersistentConfigRepo getInstance() {
		return CdiUtils.getInstance(PersistentConfigRepo.class);
	}

	/**
	 * @return
	 */
	public PersistentConfig findLatest() {
		return findByName("default");
	}

}
