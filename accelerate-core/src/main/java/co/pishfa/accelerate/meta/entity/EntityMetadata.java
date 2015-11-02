/**
 * 
 */
package co.pishfa.accelerate.meta.entity;

import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.entity.common.Entity;

/**
 * Meta information about an entity.
 * 
 * @author Taha Ghasemi
 * 
 * @param <T>
 *            the actual entity class
 * 
 */
public interface EntityMetadata<T extends Entity<K>,K> extends Entity<K> {

	EntityRepository<T,K> getRepository();

	/**
	 * TODO: must be removed
	 * @param repository
	 */
	void setRepository(EntityRepository<T,K> repository);

	Class<T> getEntityClass();
    Class<K> getKeyClass();

	String getAction(String name);

}
