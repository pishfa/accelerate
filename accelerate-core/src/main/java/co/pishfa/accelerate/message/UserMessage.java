/**
 * 
 */
package co.pishfa.accelerate.message;

import java.util.Arrays;


/**
 * @author Taha Ghasemi
 * 
 */
public class UserMessage {

	private UserMessageSeverity severity;
	private String key;
	private Object[] params;
	private String target;

	public UserMessage(UserMessageSeverity severity, String message, String target, Object... params) {
		this.severity = severity;
		this.key = message;
		this.target = target;
		this.params = params;
	}

	public UserMessageSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(UserMessageSeverity severity) {
		this.severity = severity;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String message) {
		this.key = message;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "UserMessage [severity=" + severity + ", key=" + key + ", target=" + target + ", params="
				+ Arrays.toString(params) + "]";
	}

}
