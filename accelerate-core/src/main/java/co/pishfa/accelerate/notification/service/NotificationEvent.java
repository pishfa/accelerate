package co.pishfa.accelerate.notification.service;

import co.pishfa.accelerate.notification.Notification;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class NotificationEvent {
    private Notification notification;

    public NotificationEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
