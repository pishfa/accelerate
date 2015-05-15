/**
 * 
 */
package co.pishfa.accelerate.service;

import co.pishfa.accelerate.log.LoggerHolder;
import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.repository.EntityRepository;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * An entity service that delegates all of its operations to the underlying getRepository(). Note that find* operations
 * are not secured by default.
 * 
 * @author Taha Ghasemi
 * 
 */
public abstract class BaseEntityService<T extends Entity<K>, K> implements EntityService<T,K>, LoggerHolder {

	@Inject
	private Logger log;

	public abstract EntityRepository<T,K> getRepository();

	@Override
	public Logger getLogger() {
		return log;
	}

	@Override
	@Action
	public T add(@NotNull T entity) {
		return getRepository().add(entity);
	}

	@Override
	@Action
	public T edit(@NotNull T entity) {
		return getRepository().edit(entity);
	}

	@Override
	@Action
	public void delete(@NotNull T entity) {
		getRepository().delete(entity.getId());
	}

	@Override
	public EntityMetadata<T,K> getEntityMetadata() {
		return getRepository().getEntityMetadata();
	}

	@Override
	public List<T> find(Filter<T> filter) {
		return getRepository().find(filter);
	}

	@Override
	public List<T> findAll() {
		return getRepository().findAll();
	}

	@Override
	public T findById(K id) {
		return getRepository().findById(id);
	}

	@Override
	public T findByName(String name) {
		return getRepository().findByName(name);
	}

	@Override
	public T loadById(K id) {
		return getRepository().loadById(id);
	}

	@Override
	public int count() {
		return getRepository().count();
	}

	@Override
	public int count(Filter<T> filter) {
		return getRepository().count(filter);
	}

	@Override
	public T refresh(@NotNull T entity) {
		return getRepository().refresh(entity);
	}

	@Override
	public T newEntity() throws Exception {
		return getEntityMetadata().getEntityClass().newInstance();
	}

	@Override
	public String getAction(String name) {
		return getEntityMetadata().getAction(name);
	}

}
