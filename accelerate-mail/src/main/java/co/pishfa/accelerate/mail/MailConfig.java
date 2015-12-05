/**
 * 
 */
package co.pishfa.accelerate.mail;

import javax.enterprise.context.ApplicationScoped;

import co.pishfa.accelerate.config.cdi.ConfigGetter;
import org.codemonkey.simplejavamail.TransportStrategy;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class MailConfig {

	@ConfigGetter("mail.host")
	public String getHost() {
		return null;
	}

	@ConfigGetter("mail.port")
	public Integer getPort() {
		return null;
	}

	@ConfigGetter("mail.username")
	public String getUsername() {
		return null;
	}

	@ConfigGetter("mail.password")
	public String getPassword() {
		return null;
	}

	@ConfigGetter("mail.transport")
	public TransportStrategy getTransport() {
		return null;
	}

}
