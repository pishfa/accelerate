package co.pishfa.accelerate.persistence.repository;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.accelerate.persistence.Primary;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.query.JpaQueryBuilder;
import co.pishfa.accelerate.reflection.ReflectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * A persistent, JPA based implementation of {@link EntityRepository}. All methods use {@link #getEntityManager()} to
 * obtain the {@link EntityManager}.
 *
 * @author Taha Ghasemi
 */
public abstract class BaseJpaRepo<T extends Entity<K>, K> implements EntityRepository<T, K> {

    private static final Logger log = LoggerFactory.getLogger(BaseJpaRepo.class);

    // public static final String HINT_CACHEABLE = "org.hibernate.cacheable";
    // public static final String HINT_READ_ONLY = "org.hibernate.readOnly";
    // public static final String HINT_READ_ONLY = QueryHints.READ_ONLY;

    private EntityMetadata<T, K> entityMetadata;

    @Inject
    @Primary
    private EntityManager entityManager;

    public BaseJpaRepo(Class<T> entityClass, Class<K> keyClass) {
        if (entityClass != null) {
            setEntityClass(entityClass, keyClass);
        }
    }

    protected void setEntityClass(Class<T> entityClass, Class<K> keyClass) {
        entityMetadata = EntityMetadataService.getInstance().getEntityMetadata(entityClass, keyClass);
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void init() {
        if (entityMetadata != null)
            entityMetadata.setRepository(CdiUtils.getInstance(getClass()));
    }

    @SuppressWarnings("unchecked")
    public BaseJpaRepo() {
        ParameterizedType type = ReflectionUtils.getParameterizedSuperClass(getClass());
        if (type == null || type.getActualTypeArguments().length < 1) {
            log.warn("Can not automatically detect the entity type from the generics arguments. Class: " + getClass());
        } else {
            Class<T> entityClass = (Class<T>) type.getActualTypeArguments()[0];
            setEntityClass(entityClass, (Class<K>) Long.class);
        }
    }

    @Override
    @Transactional
    public void delete(final K id) {
        delete(loadById(id));
    }

    /**
     * Uses the naive approach of iterating and deleting one by one which could be very slow.
     *
     * @see #deleteBulk(List)
     */
    @Override
    @Transactional
    public void delete(List<K> ids) {
        if (ids != null) {
            for (K id : ids) {
                delete(id);
            }
        }
    }

    /**
     * Note that bulk deleting has this side effect that JPA impl won't cascade delete to related entities and also it
     * won't call listeners. Also bulk operations in joined inheritance strategy has a bug in hibernate
     *
     * @param ids
     */
    @Transactional
    public void deleteBulk(List<K> ids) {
        query().delete().where("e.id in (:ids)").with("ids", ids).run();
    }

    public String entityAlias() {
        return entityMetadata.getEntityClass().getName();
    }

    @Override
    @Transactional
    public void deleteAll(List<T> entities) {
        if (entities != null) {
            for (T t : entities) {
                delete(t);
            }
        }
    }

    /**
     * Note that a detached entity could not be deleted so in these cases use {@link BaseJpaRepo#delete(K)}
     * instead.
     */
    @Override
    @Transactional
    public void delete(T obj) {
        getEntityManager().remove(obj);
    }

    @Override
    @Transactional
    public void deleteAll() {
        query().delete().run();
    }

    @Override
    public T refresh(T obj) {
        getEntityManager().refresh(obj);
        return obj;
    }

    @Override
    public List<T> find(Filter<T> filter) {
        return query().select().where(filter).sort().paged().list();
    }

    @Override
    // @SuppressWarnings("unchecked")
    public List<T> findAll() {
        /*
		 * CriteriaQuery<Object> query = builder().createQuery(); query.select(query.from(entityType.getEntityClass()));
		 * return (List<T>) entityManager().createQuery(query).getResultList();
		 */
        return query().select().list();
    }

	/*
	 * Retrieves all results ordered by give column.
	 * 
	 * @Override
	 * 
	 * @SuppressWarnings("unchecked") public List<T> findAll(String orderColumn, boolean ascending) { CriteriaBuilder
	 * builder = builder(); CriteriaQuery<Object> query = builder.createQuery(); Root<T> root =
	 * query.from(entityType.getEntityClass()); query.select(root); if (ascending)
	 * query.orderBy(builder.asc(root.get(orderColumn))); else query.orderBy(builder.desc(root.get(orderColumn)));
	 * return (List<T>) entityManager().createQuery(query).getResultList(); }
	 */

    @Override
    public T findById(final K id) {
        T res = getEntityManager().find(entityMetadata.getEntityClass(), id);
        if (res != null) {
            return res;
        }
        throw new EntityNotFoundException("Could not find an entity with class " + entityAlias() + " with id " + id);
    }

    @Override
    public T findByName(@NotNull String name) {
		/*
		 * CriteriaBuilder builder = builder(); CriteriaQuery<Object> query = builder.createQuery(); Root<T> root =
		 * query.from(entityType.getEntityClass()); query.select(root); query.where(builder.equal(root.get("name"),
		 * name)); return CommonUtilities.cast(getEntityManager().createQuery(query).getSingleResult());
		 */
        return query().select().where("e.name=:name").with("name", name).result();
    }

    /**
     * Similar to {@link BaseJpaRepo#findById} but returns an uninitialized proxy.
     */
    @Override
    public T loadById(final K id) {
        return getEntityManager().getReference(entityMetadata.getEntityClass(), id);
    }

    public void flush() {
        getEntityManager().flush();
    }

    /**
     * @return number of entities in the database
     */
    @Override
    public int count() {
		/*
		 * CriteriaBuilder builder = builder(); CriteriaQuery<K> query = builder.createQuery(K.class);
		 * query.select(builder.count(query.from(entityType.getEntityClass()))); return
		 * getEntityManager().createQuery(query).getSingleResult();
		 */
        return query().selectCount().result(Long.class).intValue();
    }

    /**
     * @return
     */
    protected CriteriaBuilder builder() {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public int count(Filter<T> filter) {
        return query().selectCount().where(filter).result(Long.class).intValue();
    }

    /**
     * Don't make it public since we don't like entityManage to be exposed to higher layers.
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    @Transactional
    public T add(T obj) {
        // persist is problematic for objects that have association with other already persisted objects
		/*entityManager().persist(obj);
		return obj;*/
        return getEntityManager().merge(obj);
    }

    @Override
    @Transactional
    public void add(List<T> entities) {
        Validate.notNull(entities);
        for (T entity : entities) {
            add(entity);
        }
    }

    @Override
    @Transactional
    public T edit(T obj) {
        return getEntityManager().merge(obj);
    }

    public void clear() {
        getEntityManager().clear();
    }

    /**
     * Don't make it public since we don't like query to be exposed to higher layers.
     *
     * @return
     */
    public JpaQueryBuilder<T> query() {
        return query("");
    }

    /**
     * Don't make it public since we don't like query to be exposed to higher layers.
     *
     * @return
     */
    public JpaQueryBuilder<T> query(String queryHead) {
        return new JpaQueryBuilder<T>(this.getEntityManager(), this.entityMetadata.getEntityClass(), queryHead);
    }

    @Override
    public EntityMetadata<T, K> getEntityMetadata() {
        return entityMetadata;
    }

    @Override
    @Transactional
    public T save(T entity) {
        if (entity.getId() == null) {
            return add(entity);
        } else {
            return edit(entity);
        }
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
