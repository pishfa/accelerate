/**
 * 
 */
package co.pishfa.accelerate.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.message.UserMessage;
import co.pishfa.accelerate.message.UserMessageSeverity;

/**
 * @author Taha Ghasemi
 * 
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private List<UserMessage> messages;

	public ValidationException(String messageKey) {
		this(new UserMessage(UserMessageSeverity.ERROR, messageKey, null));
	}

	public ValidationException(String messageKey, String target) {
		this(new UserMessage(UserMessageSeverity.ERROR, messageKey, target));
	}

	public ValidationException(UserMessage message) {
		Validate.notNull(message);

		this.messages = new ArrayList<UserMessage>();
		this.messages.add(message);
	}

	public ValidationException(List<UserMessage> messages) {
		Validate.notNull(messages);
		this.messages = messages;
	}

	public List<UserMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<UserMessage> messages) {
		this.messages = messages;
	}

	public void addMessage(UserMessage message) {
		messages.add(message);
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		for (UserMessage message : messages) {
			sb.append(message.getKey()).append(',');
		}
		return sb.toString();
	}

}
