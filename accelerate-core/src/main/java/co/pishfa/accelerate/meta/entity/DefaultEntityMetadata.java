/**
 * 
 */
package co.pishfa.accelerate.meta.entity;

import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.repository.EntityRepository;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Taha Ghasemi
 * 
 */
@javax.persistence.Entity
@Table(name = "ac_default_entity_metadata")
public class DefaultEntityMetadata extends BaseEntity implements EntityMetadata<Entity<Long>,Long> {

	private static final long serialVersionUID = 1L;

	@Transient
	private Class<Entity<Long>> entityClass;

	@Transient
	private EntityRepository<Entity<Long>,Long> repository;

	/**
	 * @param repository
	 *            the repository to set
	 */
	@Override
	public void setRepository(EntityRepository<Entity<Long>,Long> repository) {
		this.repository = repository;
	}

	@Override
	public EntityRepository<Entity<Long>,Long> getRepository() {
		return repository;
	}

	@Override
	public Class<Entity<Long>> getEntityClass() {
		return entityClass;
	}

	/**
	 * @param entityClass
	 *            the entityClass to set
	 */
	public void setEntityClass(Class<Entity<Long>> entityClass) {
		this.entityClass = entityClass;
	}

	protected String getActionSet() {
		return StringUtils.uncapitalize(entityClass.getSimpleName());
	}

	@Override
	public String getAction(String name) {
		return getActionSet() + "." + name;
	}

    @Override
    public Class<Long> getKeyClass() {
        return Long.class;
    }
}
