package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.User;

import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class UserRepo extends BaseJpaRepo<User, Long> {

	public static final String GUEST_NAME = "guest";
	public static final String ADMIN_NAME = "admin";
	public static final String SYSTEM_NAME = "system";
	public static final String SHARED_NAME = "shared";

	private Long adminId, guessId, systemId, sharedId;

	public static UserRepo getInstance() {
		return CdiUtils.getInstance(UserRepo.class);
	}

	public User findAdmin() {
		if (adminId == null) {
			adminId = findByName(ADMIN_NAME).getId();
		}
		return findById(adminId);
	}

	public User findShared() {
		return findById(getSharedUserId());
	}

	public Long getSharedUserId() {
		if (sharedId == null) {
			sharedId = findByName(SHARED_NAME).getId();
		}
		return sharedId;
	}

	public User findSystemUser() {
		if (systemId == null) {
			systemId = findByName(SYSTEM_NAME).getId();
		}
		return findById(systemId);
	}

	public User findGuest() {
		if (guessId == null) {
			guessId = findByName(GUEST_NAME).getId();
		}
		return findById(guessId);
	}

	@SuppressWarnings("unchecked")
	public List<User> findUsersWithUsername(Collection<String> usernames) {
		// We need to batch this because we use an in() expression
		int batchsize = 50;
		int i = 0;
		List<String> usernamesToQuery = new ArrayList<String>(batchsize);
		List<User> users = new ArrayList<User>();
		for (String username : usernames) {
			usernamesToQuery.add(username);
			i++;
			if (i % batchsize == 0 || usernames.size() < batchsize) {
				// Query and clear
				Query q = getEntityManager().createQuery(
						"select u from User u left join fetch u.person where u.name in(:usernames)");
				q.setParameter("usernames", usernamesToQuery);
				users.addAll(q.getResultList());
				usernamesToQuery.clear();
			}
		}
		return users;
	}

	@Override
	public User findByName(@NotNull String username) {
		return (User) getEntityManager().createNamedQuery("User.findByName").setParameter(1, username).getSingleResult();
	}

    public List<User> findByDomain(@NotNull Domain domain) {
        return query().fromEntity().whereTrue().andEntityFieldEquals("domain", domain).sortBy("e.person.lname,e.person.fname").list();
    }

	@QueryRunner
	public boolean checkByName(String username) {
		return false;
	}

}
