/**
 * 
 */
package co.pishfa.accelerate.mail;

import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import co.pishfa.accelerate.core.ConfigAppliedEvent;
import co.pishfa.accelerate.message.MessageFormatter;
import co.pishfa.accelerate.message.Messages;
import co.pishfa.accelerate.notification.Notification;
import co.pishfa.accelerate.notification.service.NotificationProvider;
import co.pishfa.accelerate.service.Service;
import co.pishfa.security.entity.authentication.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import org.slf4j.Logger;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Service
public class MailService implements NotificationProvider {

	@Inject
	private MailConfig config;

	private Mailer mailer;

	@Inject
	private Logger log;

	@Inject
	private Messages messages;

	public void onEvent(@Observes ConfigAppliedEvent event) {
		if (config.getHost() != null) {
			mailer = new Mailer(config.getHost(), config.getPort(), config.getUsername(), config.getPassword(),
					config.getTransport());
		}
	}

	public void send(@NotNull final String mailId, Map<String, Object> params) {
		// TODO read mails.xml
	}

	public void send(@NotNull final Email email) throws Exception {
		Validate.notNull(mailer);
		mailer.sendMail(email);
	}

	public void send(@NotNull final List<Email> emails) throws Exception {
		Validate.notNull(mailer);
		// TODO add bulk email sending support
		for (Email email : emails) {
			mailer.sendMail(email);
		}
	}

	public String getName() {
		return "mail";
	}

	public void notify(Notification notification) {
		for(User to : notification.getTo()) {
			if(!StringUtils.isBlank(to.getEmail())) {
				Email email = new Email.Builder()
						.from(messages.get("app.title"), config.getUsername())
						.to(to.getTitle(), to.getEmail())
						.subject(notification.getTitle())
						.text(notification.getMessage())
						.build();
				try {
					send(email);
				} catch (Exception e) {
					log.error("Failed to send notification email to " + to.getEmail(), e);
				}
			}
		}
	}
}
