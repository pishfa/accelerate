/**
 * 
 */
package co.pishfa.accelerate.persistence.repository;

import co.pishfa.accelerate.service.EntityService;
import co.pishfa.accelerate.entity.common.Entity;

import java.util.List;

/**
 * Represents a repository of entities of a certain type. The repository is responsible for cascading the operations
 * properly (if required) but the business logic of entities during add, edit, or delete is not its business. As a rule,
 * all required queries from a repository should be implemented as a method whose name is satisfies this pattern
 * findXByYOrderByZ. Unless specified in javadoc, those finders that return a single result should throw exception if
 * there is no result or more than one result but those finders that return a list should always return a not-null list.
 * In all methods with a filter as an input, filter can be null.
 * 
 * 
 * @author Taha Ghasemi
 * 
 * @param <T>
 *            Entities type
 * @param <K>
 *            Entities key type
 */
public interface EntityRepository<T extends Entity<K>, K> extends EntityService<T,K> {

	/**
	 * Adds or edits based on whether entity has id or not
	 */
	T save(T entity);

	void add(List<T> entities);

	void delete(final K id);

	void deleteAll();

	void deleteAll(List<T> entities);

	void delete(List<K> ids);

	// We don't like externalizing the query interface to other layers since it causes spreading of the query related
	// issues.
	// public QueryBuilder<T> query();

}