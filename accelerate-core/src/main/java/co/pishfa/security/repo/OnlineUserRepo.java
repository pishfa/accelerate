/**
 * 
 */
package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.OnlineUser;
import co.pishfa.security.entity.authentication.User;

import java.util.Collection;
import java.util.List;

/**
 * @author Taha Ghasemi
 * 
 */
@Repository
public class OnlineUserRepo extends BaseJpaRepo<OnlineUser, Long> {

	/**
	 * @param user
	 * @return
	 */
	@QueryRunner("select count(e) from OnlineUser e where e.user = ?1 and e.loggedIn = true")
	public long getCountByUser(User user) {
		return 0;
	}

	/**
	 * @param userNames
	 * @return
	 */
	public List<OnlineUser> findByUserNames(Collection<String> userNames) {
		return query().select().where("user.name in (:userNames)").with("userNames", userNames).list();
	}

}
