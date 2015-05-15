/**
 * 
 */
package co.pishfa.accelerate.meta.domain;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.meta.entity.DefaultEntityMetadata;
import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.repository.GenericJpaRepo;
import co.pishfa.accelerate.utility.CommonUtils;
import org.apache.commons.lang3.Validate;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class EntityMetadataService {

	@Inject
	private Instance<GenericJpaRepo<? extends Entity<Long>,Long>> repositoryInstance;

	public static EntityMetadataService getInstance() {
		return CdiUtils.getInstance(EntityMetadataService.class);
	}

	private final Map<Class<?>, EntityMetadata<?,?>> entityMetadataMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T extends Entity<K>,K> EntityMetadata<T,K> getEntityMetadata(Class<T> entityClass,Class<K> keyClass) {
		Validate.notNull(entityClass);

		EntityMetadata<?,?> entityType = entityMetadataMap.get(entityClass);
		if (entityType != null) {
			return (EntityMetadata<T,K>) entityType;
		} else {
			DefaultEntityMetadata defaultEntityMetadata = new DefaultEntityMetadata();
			defaultEntityMetadata.setEntityClass((Class<Entity<Long>>) entityClass);
			entityMetadataMap.put(entityClass, defaultEntityMetadata); // this should be before next line since repository
																		// call this method again
			setEntityRepository(defaultEntityMetadata);
			return CommonUtils.cast(defaultEntityMetadata);
		}
	}

	private <T extends Entity<Long>> void setEntityRepository(EntityMetadata<T,Long> entityMetadata) {
        GenericJpaRepo<? extends Entity<Long>,Long> repository = CommonUtils.cast(repositoryInstance.get());
		((GenericJpaRepo<? extends Entity<Long>,Long>) repository).setEntityClass((Class) entityMetadata.getEntityClass(),Long.class);
		entityMetadata.setRepository((EntityRepository) repository);
	}

}
