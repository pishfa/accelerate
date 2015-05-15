package co.pishfa.accelerate.config.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.entity.PersistentConfig;
import co.pishfa.accelerate.config.entity.PersistentConfigEntry;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;

/**
 * The repository for {@link co.pishfa.accelerate.config.entity.PersistentConfigEntry} entities.
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PersistentConfigEntryRepo extends BaseJpaRepo<PersistentConfigEntry, Long> {

	public static PersistentConfigEntryRepo getInstance() {
		return CdiUtils.getInstance(PersistentConfigEntryRepo.class);
	}

    @QueryRunner(where="e.config = ?1 and e.name = ?2", nullOnNoResult = true)
    public PersistentConfigEntry findByConfigAndName(PersistentConfig persistentConfig, String name) {
        return null;
    }
}
