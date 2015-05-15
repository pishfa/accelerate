package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.security.entity.authorization.Permission;
import co.pishfa.security.entity.authorization.Role;
import co.pishfa.security.entity.authorization.SecurityLevel;
import co.pishfa.security.service.AuthenticationService;
import co.pishfa.security.service.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.Valid;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_user", uniqueConstraints = {@UniqueConstraint(columnNames = "name"), @UniqueConstraint(columnNames = "email")})
@DiscriminatorColumn(discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("u")
@NamedQueries({
		@NamedQuery(name = "User.findByName", query = "select u from User u join fetch u.person where u.name = ?1"),
		@NamedQuery(name = "User.checkByName", query = "select count(c) from User c where c.name=?1") })
@InitEntity(properties = @InitProperty(name = "domain", value = "@parent?"))
public class User extends Principal {

	private static final Logger log = LoggerFactory.getLogger(User.class);

	private static final long serialVersionUID = 1L;

	/*
	 * @NotEmpty
	 * 
	 * @Index(name = "nameIndex", columnNames = { "name" })
	 * 
	 * @Length(max = 100)
	 * 
	 * @Column(updatable = false) protected String username;
	 */

	@Valid
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	protected LoginInfo loginInfo;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	protected Profile profile;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
	protected Person person;

	@Enumerated(EnumType.ORDINAL)
	protected SecurityLevel currentLevel = SecurityLevel.UNCLASSIFIED;

	protected boolean forcedToChangePass = false;

	@Transient
	protected String password;

	private String language;

	private String country;

	private String timeZone;

    private String email;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private File image;

	public User() {
		this.loginInfo = new LoginInfo();
		this.profile = new Profile(this);
		this.person = new Person();
	}

	public User(String name, String password) {
		this();
		setName(name);
		try {
			this.setPassword(password);
		} catch (GeneralSecurityException e) {
			log.error("", e);
		}
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public SecurityLevel getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(SecurityLevel currentLevel) {
		this.currentLevel = currentLevel;
	}

	public LoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(LoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	@Override
	public boolean isActive() {
		return activation.isActive()
				&& (getDomain() == null || getDomain().isActive());
	}

	public void setPassword(String password) throws GeneralSecurityException {
		loginInfo.setPasswordHash(AuthenticationService.getInstance().hashPassword(password));
		loginInfo.setLastPasswordChange(new Date());
		forcedToChangePass = false;
		this.password = password; // only temporarily
	}

	public String getPassword() {
		return password;
	}

	public void setSecurityAnswer(String answer) throws GeneralSecurityException {
		if (!StringUtils.isEmpty(answer)) {
			loginInfo.setSecurityAnswerHash(AuthenticationService.getInstance().hashSecurityAnswer(answer));
		}
	}

	public String getSecurityAnswer() {
		return null;
	}

	public boolean needToChangePassword() {
		if (forcedToChangePass) {
			return true;
		}
		Integer expirationPeriod = getDomain().getSecurityPolicyInherited().getExpirationPeriod();
		if (expirationPeriod != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -expirationPeriod);
			return calendar.after(getLoginInfo().getLastPasswordChange());
		}

		return false;
	}

	public boolean isForcedToChangePass() {
		return forcedToChangePass;
	}

	public void setForcedToChangePass(boolean forcedToChangePass) {
		this.forcedToChangePass = forcedToChangePass;
	}

	public void setCurrentLevelByName(String name) {
		setCurrentLevel(SecurityLevel.valueOf(name));
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Override
	public int getPrecedenceBase() {
		return 10000;
	}

	@Override
	public String getTitle() {
		return person.getTitle();
	}

	@Override
	public PrincipalType getType() {
		return PrincipalType.USER;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}
