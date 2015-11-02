/**
 * 
 */
package co.pishfa.accelerate.service;

import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.filter.Filter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A service for operations of one entity type.
 * 
 * 
 * @author Taha Ghasemi
 * 
 * @param <T>
 *            Entities type
 * @param <K>
 *            Entities key type
 */
public interface EntityService<T extends Entity<K>, K> extends Serializable {

	EntityMetadata<T,K> getEntityMetadata();

	/**
	 * Finds those entities that match with the filter.
	 * 
	 * @param filter
	 *            . Can be null.
	 */
	List<T> find(Filter<T> filter);

	List<T> findAll();

	/**
	 * Finds with the given id. Throws exception if nothing found.
	 */
	T findById(final K id);

	/**
	 * Finds the entity with the given name. Throws exception if nothing found or more than one entity with this name is
	 * found.
	 */
	T findByName(@NotNull final String name);

	/**
	 * Similar to {@link EntityService#findById(K)} but returns an uninitialized proxy.
	 */
	T loadById(final K id);

	/**
	 * 
	 * @return number of entities in the database
	 */
	int count();

	/**
	 * @param filter
	 *            . Can be null.
	 * @return number of entities that match with the filter.
	 */
	int count(Filter<T> filter);

	T add(@NotNull T entity);

	T edit(@NotNull T entity);

	void delete(@NotNull T entity);

	/**
	 * The entity must be managed
	 */
	T refresh(@NotNull T entity);

	T newEntity() throws Exception;

	/**
	 * Get full security action corresponding to the given action name on the entity of this service. e.g. given
	 * name='add' when entity service is CityService it might return city.add.
	 */
	String getAction(String name);

}