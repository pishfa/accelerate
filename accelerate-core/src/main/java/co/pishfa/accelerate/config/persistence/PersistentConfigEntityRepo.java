package co.pishfa.accelerate.config.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.config.entity.PersistentConfig;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.service.PersistentConfigEntity;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Repository
public class PersistentConfigEntityRepo extends BaseJpaRepo<PersistentConfigEntity, Long> {

    public static PersistentConfigEntityRepo getInstance() {
        return CdiUtils.getInstance(PersistentConfigEntityRepo.class);
    }

    public PersistentConfigEntity findLatestByName(Class<? extends PersistentConfigEntity> entityClass, String name, PersistentConfig config) {
        return query().select(entityClass.getName()).whereTrue().andEntityFieldEquals("config", config)
                .andEntityFieldEquals("name", name).max(1).result();
    }
}
