package co.pishfa.accelerate.notification.service;

import co.pishfa.accelerate.notification.Notification;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public interface NotificationProvider {

    String getName();
    void notify(Notification notification);

}
