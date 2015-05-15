/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.entity.common.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_login_info")
public class LoginInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	protected String passwordHash;

	/**
	 * Number of consecutive failed attempts
	 */
	protected int loginAttempts = 0;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date lastLoginTime;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date lastPasswordChange;

	protected String lastUrl;

	protected String securityQuestion;
	protected String securityAnswerHash;

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public int getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(int loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastPasswordChange() {
		return lastPasswordChange;
	}

	public void setLastPasswordChange(Date lastPasswordChange) {
		this.lastPasswordChange = lastPasswordChange;
	}

	public String getLastUrl() {
		return lastUrl;
	}

	public void setLastUrl(String lastUrl) {
		this.lastUrl = lastUrl;
	}

	public String getSecurityQuestion() {
		return securityQuestion;
	}

	public void setSecurityQuestion(String securityQuestion) {
		this.securityQuestion = securityQuestion;
	}

	public String getSecurityAnswerHash() {
		return securityAnswerHash;
	}

	public void setSecurityAnswerHash(String questionAnswerHash) {
		this.securityAnswerHash = questionAnswerHash;
	}

}
