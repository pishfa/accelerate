package co.pishfa.accelerate.meta.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.accelerate.meta.entity.DefaultEntityMetadata;

/**
 * The repository for {@link DefaultEntityMetadata} entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class DefaultEntityTypeRepo extends BaseJpaRepo<DefaultEntityMetadata, Long> {

	public static DefaultEntityTypeRepo getInstance() {
		return CdiUtils.getInstance(DefaultEntityTypeRepo.class);
	}

}
