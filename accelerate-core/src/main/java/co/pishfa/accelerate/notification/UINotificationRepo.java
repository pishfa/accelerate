package co.pishfa.accelerate.notification;

import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.User;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Repository
public class UINotificationRepo extends BaseJpaRepo<UINotification, Long> {

    @QueryRunner("select count(e) from UINotification e where e.to = ?1 and e.read = false")
    public long countUnread(User user) {
        return 0;
    }

    @QueryRunner("update UINotification e set e.read = true where e.to = ?1")
    public void markAllAsRead(User user) {

    }
}
