package co.pishfa.accelerate.persistence.repository;

import co.pishfa.accelerate.entity.common.Entity;

/**
 * A repository for the given entity type.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class GenericJpaRepo<T extends Entity<K>,K> extends BaseJpaRepo<T, K> {

	public GenericJpaRepo() {
		super(null,null);
	}

	public GenericJpaRepo(Class<T> entityClass, Class<K> keyClass) {
		super(entityClass,keyClass);
	}

	@Override
	public void setEntityClass(Class<T> entityClass,Class<K> keyClass) {
		super.setEntityClass(entityClass,keyClass);
	}

}
