/**
 * 
 */
package co.pishfa.accelerate.initializer;

import co.pishfa.accelerate.initializer.core.BaseInitListener;
import co.pishfa.accelerate.initializer.model.InitEntityMetadata;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.Primary;
import co.pishfa.accelerate.persistence.query.JpaQueryBuilder;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Taha Ghasemi
 * 
 */
public class DbInitListener extends BaseInitListener {

	@Inject
	private Logger log;

	@Inject
    @Primary
	private EntityManager entityManager;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void entityCreated(final InitEntityMetadata initEntity, final Object entityObj) {
		Entity obj = (Entity) entityObj;
		if (obj.getId() == null) {
			entityManager.persist(obj);
		} else {
			entityManager.merge(obj);
		}
	}

	@Override
	public void entityFinished(final InitEntityMetadata initEntity, final Object entityObj) {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object findEntity(final InitEntityMetadata initEntity, Map<String, Object> propValues) {
		JpaQueryBuilder<Entity> q = new JpaQueryBuilder(entityManager, initEntity.getEntityClass(), "");
		q.select().whereTrue();

		for (Entry<String, Object> pv : propValues.entrySet()) {
			q.andEntityFieldEquals(pv.getKey(), pv.getValue());
		}

		try {
			return q.max(1).result();
		} catch (NoResultException | NonUniqueResultException nur) {
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

}
