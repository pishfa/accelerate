/**
 * 
 */
package co.pishfa.security.entity.authentication;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Taha Ghasemi
 * 
 *         TODO model exact enable timing like 8am-18pm monday-thusday
 * 
 */
@Embeddable
public class Activation implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean enabled = true;

	@Temporal(TemporalType.TIMESTAMP)
	private Date start;

	@Temporal(TemporalType.TIMESTAMP)
	private Date end;

	private boolean removeAfterExpiration = false;

	public Date getStart() {
		return start;
	}

	public void setStart(Date activiationStart) {
		this.start = activiationStart;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date activationEnd) {
		this.end = activationEnd;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isActive() {
		if (!enabled) {
			return false;
		}

		Date now = new Date();
		if (start != null && now.before(start)) {
			return false;
		}

		if (end != null && now.after(end)) {
			return false;
		}

		return true;
	}

	public boolean isRemoveAfterExpiration() {
		return removeAfterExpiration;
	}

	public void setRemoveAfterExpiration(boolean removeAfterExpiration) {
		this.removeAfterExpiration = removeAfterExpiration;
	}

}
