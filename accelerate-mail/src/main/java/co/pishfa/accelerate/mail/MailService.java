/**
 * 
 */
package co.pishfa.accelerate.mail;

import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.Validate;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;

import co.pishfa.accelerate.core.ConfigurationAppliedEvent;
import co.pishfa.accelerate.domain.Service;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Service
public class MailService {

	@Inject
	private MailConfig config;

	private Mailer mailer;

	public void onEvent(@Observes ConfigurationAppliedEvent event) {
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
}
