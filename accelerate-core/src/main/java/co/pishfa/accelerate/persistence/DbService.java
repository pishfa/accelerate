/**
 * 
 */
package co.pishfa.accelerate.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.service.Service;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class DbService {

    public static DbService getInstance() {
        return CdiUtils.getInstance(DbService.class);
    }

    @PersistenceUnit(unitName = "data")
	private EntityManagerFactory emf;

    @Inject
    @Primary
    private EntityManager entityManager;

	@Produces
    @Primary
	@RequestScoped
	public EntityManager getPrimaryEntityManager() {
        if(emf == null) {
           emf = Persistence.createEntityManagerFactory("data");
        }
		EntityManager entityManager = emf.createEntityManager();
		/*((ClientSession) ((EntityManagerImpl) (entityManager)).getUnitOfWork().getParent()).getConnectionPolicy()
				.setPoolName(null);*/
		return entityManager;
	}

    @Produces
    @Default
    @RequestScoped
    public EntityManager getDefaultEntityManager() {
        return entityManager;
    }

	public void closePrimary(@Disposes @Primary final EntityManager entityManager) {
		entityManager.close();
	}

}
