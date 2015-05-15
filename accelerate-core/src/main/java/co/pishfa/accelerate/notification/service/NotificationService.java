package co.pishfa.accelerate.notification.service;

import co.pishfa.accelerate.notification.Notification;
import co.pishfa.accelerate.service.Service;

import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class NotificationService {

    @Inject
    private Event<NotificationEvent> onNotify;

    public void notify(Notification notification) {
        onNotify.fire(new NotificationEvent(notification));
    }


}
