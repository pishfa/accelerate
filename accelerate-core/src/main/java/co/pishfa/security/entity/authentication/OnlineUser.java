/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.http.SessionManager;
import co.pishfa.accelerate.utility.TimeUtils;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_online_user")
public class OnlineUser extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.MERGE })
	protected User user;

	protected String sessionId;

	@Transient
	protected HttpSession session;

	protected boolean loggedIn = false;

	public OnlineUser(User user, String sessionId) {
		setUser(user);
		this.sessionId = sessionId;
	}

	public OnlineUser() {
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		Validate.notNull(user);
		this.user = user;
		this.domain = user.getDomain(); // secDomain is the secDomain of its corresponding user
	}

	public Date getLastAccessedTime() {
		return getSession() == null? null : new Date(getSession().getLastAccessedTime());
	}

	public Date getCreationTime() {
		return getSession() == null? null : new Date(getSession().getCreationTime());
	}

	public HttpSession getSession() {
		if (session == null) {
			session = SessionManager.getSessions().get(sessionId);
		}
		return session;
	}

	public long getSessionSize() {
		return SessionManager.getSessionSize(sessionId);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getIdleTime() {
		return getSession() == null? 0 : TimeUtils.since(getSession().getLastAccessedTime());
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

}
