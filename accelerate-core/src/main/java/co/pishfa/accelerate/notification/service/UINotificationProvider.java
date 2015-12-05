package co.pishfa.accelerate.notification.service;

import co.pishfa.accelerate.message.MessageFormatter;
import co.pishfa.accelerate.message.Messages;
import co.pishfa.accelerate.notification.Notification;
import co.pishfa.accelerate.notification.UINotification;
import co.pishfa.accelerate.notification.UINotificationRepo;
import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.service.Service;
import co.pishfa.security.entity.authentication.User;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class UINotificationProvider extends BaseEntityService<UINotification, Long> implements NotificationProvider {

    @Inject
    private UINotificationRepo uiNotificationRepo;

    @Override
    public String getName() {
        return "ui";
    }

    public void notify(Notification src) {
        for(User to : src.getTo()) {
            UINotification n = new UINotification();
            n.setTitle(src.getTitle());
            n.setCreationTime(src.getCreationTime());
            n.setFrom(src.getFrom());
            n.setMessage(src.getMessage());
            n.setTo(to);
            add(n);
        }
    }

    @Override
     public EntityRepository<UINotification, Long> getRepository() {
        return uiNotificationRepo;
    }

    public long countUnread(User user) {
        return uiNotificationRepo.countUnread(user);
    }

    @Transactional
    public void markAllAsRead(User user) {
        uiNotificationRepo.markAllAsRead(user);
    }
}
