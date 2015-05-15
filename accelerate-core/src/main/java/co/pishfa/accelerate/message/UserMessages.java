/**
 * 
 */
package co.pishfa.accelerate.message;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.ui.UiUtils;

/**
 * This class is a bridge that lower layers such as business layer can send their messages to the user interface to be
 * displayed for users.
 * 
 * TODO only works when FacesContext is available e.g. in async methods it has problem
 * 
 * @author Taha Ghasemi
 * 
 */
@ApplicationScoped
public class UserMessages implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MessageFormatter formatter;

	@Inject
	private Messages messages;

	public static UserMessages getInstance() {
		return CdiUtils.getInstance(UserMessages.class);
	}

	public FacesMessage create(UserMessage message) {
		return create(message.getSeverity(), message.getKey(), message.getParams());
	}

	public FacesMessage create(UserMessageSeverity severity, final String key, final Object... params) {
		String summary = formatter.format(messages.get(key), params);
		return new FacesMessage(severity.getFacesSeverity(), summary, null);
	}

	public void add(final UserMessageSeverity severity, final String key, final Object... params) {
		add(null, severity, key, params);
	}

	public void add(UserMessage message) {
		add(message.getTarget(), message.getSeverity(), message.getKey(), message.getParams());
	}

	public void add(final String clientId, final UserMessageSeverity severity, final String key,
			final Object... params) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			facesContext.addMessage(clientId, create(severity, key, params));
		} else {
			throw new IllegalStateException("No faces context is available");
		}
	}

	public void info(final String key, final Object... params) {
		add(UserMessageSeverity.INFO, key, params);
	}

	public void warn(final String key, final Object... params) {
		add(UserMessageSeverity.WARN, key, params);
	}

	public void error(final String key, final Object... params) {
		add(UserMessageSeverity.ERROR, key, params);
	}

	public void keepMessages() {
		UiUtils.getExternalContext().getFlash().setKeepMessages(true);
	}

}
