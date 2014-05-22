/**
 * 
 */
package co.pishfa.accelerate.mail;

import javax.enterprise.context.ApplicationScoped;

import org.codemonkey.simplejavamail.TransportStrategy;

import co.pishfa.accelerate.config.cdi.Configured;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class MailConfig {

	@Configured("mail.host")
	public String getHost() {
		return null;
	}

	@Configured("mail.port")
	public Integer getPort() {
		return null;
	}

	@Configured("mail.username")
	public String getUsername() {
		return null;
	}

	@Configured("mail.password")
	public String getPassword() {
		return null;
	}

	@Configured("mail.transport")
	public TransportStrategy getTransport() {
		return null;
	}

}
