package co.pishfa.accelerate.notification.service;

import co.pishfa.accelerate.async.Async;
import co.pishfa.accelerate.message.MessageFormatter;
import co.pishfa.accelerate.message.Messages;
import co.pishfa.accelerate.notification.Notification;
import co.pishfa.accelerate.service.Service;
import co.pishfa.security.repo.UserRepo;
import org.slf4j.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class NotificationService {

    @Inject
    private Logger log;

    @Inject
    private Instance<NotificationProvider> providers;

    private Map<String,NotificationProvider> providerMap;

    @Inject
    private MessageFormatter formatter;

    @Inject
    private Messages messages;

    @Inject
    private UserRepo userRepo;

    @Async
    public void notify(Notification notification, String... targets) {
        if(targets == null || targets.length == 0)
            for(NotificationProvider provider : providers)
                provider.notify(notification);
        else {
            if (providerMap == null) {
                providerMap = new HashMap<>();
                for (NotificationProvider provider : providers)
                    providerMap.put(provider.getName(), provider);
            }
            for(String target : targets) {
                NotificationProvider provider = providerMap.get(target);
                if(provider != null) {
                    notification.setMessage(formatter.format(messages.get(notification.getMessage()+"."+provider.getName()), notification.getParameters()));
                    for(int i = 0; i < notification.getTo().size(); i++) {
                        notification.getTo().set(i, userRepo.findById(notification.getTo().get(i).getId()));
                    }
                    provider.notify(notification);
                } else {
                    log.warn("No notification provider with name {} is found.",target);
                }
            }

        }

    }


}
