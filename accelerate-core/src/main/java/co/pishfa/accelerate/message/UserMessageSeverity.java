/**
 * 
 */
package co.pishfa.accelerate.message;

import javax.faces.application.FacesMessage;

/**
 * @author Taha Ghasemi
 * 
 */
public enum UserMessageSeverity {

	INFO, WARN, ERROR;

	public javax.faces.application.FacesMessage.Severity getFacesSeverity() {
		switch (this) {
		case INFO:
			return FacesMessage.SEVERITY_INFO;
		case WARN:
			return FacesMessage.SEVERITY_WARN;
		case ERROR:
			return FacesMessage.SEVERITY_ERROR;
		}
		return null;
	}

}
