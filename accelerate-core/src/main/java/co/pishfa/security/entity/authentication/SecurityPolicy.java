/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.i18n.domain.Locale;
import co.pishfa.accelerate.message.UserMessage;
import co.pishfa.accelerate.message.UserMessageSeverity;
import co.pishfa.accelerate.validation.ValidationException;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;
import edu.vt.middleware.password.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_security_policy")
public class SecurityPolicy extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(SecurityPolicy.class);

	public enum LoginFailAction {
		NOTHING,
		DISABLE_ACCOUNT,
		DISABLE_LIMITED_TIME
	};

	public enum PasswordStrength {
		LOW,
		MEDIUM,
		HIGH
	};

	private int minPasswordLength = 5;

	private int minUsernameLength = 5;

	private int numberOfFailedTries = 3;

	private int waitTimeForRelogin = 5; // minutes

	private boolean preventMultipleLogin = false;

	@Enumerated(EnumType.ORDINAL)
	private PasswordStrength passwordStrength = PasswordStrength.MEDIUM;

	@Enumerated(EnumType.ORDINAL)
	private LoginFailAction loginFailAction = LoginFailAction.DISABLE_ACCOUNT;

	/**
	 * how many days to expire password
	 */
	private Integer expirationPeriod = 180;

	private int sessionTimeout;

	public boolean isPreventMultipleLogin() {
		return preventMultipleLogin;
	}

	public void setPreventMultipleLogin(boolean checkOnlineUsers) {
		this.preventMultipleLogin = checkOnlineUsers;
	}

	public LoginFailAction getLoginFailAction() {
		return loginFailAction;
	}

	public void setLoginFailAction(LoginFailAction loginFailAction) {
		this.loginFailAction = loginFailAction;
	}

	public Integer getExpirationPeriod() {
		return expirationPeriod;
	}

	public void setExpirationPeriod(Integer expirationPeriod) {
		this.expirationPeriod = expirationPeriod;
	}

	public int getNumberOfFailedTries() {
		return numberOfFailedTries;
	}

	public void setNumberOfFailedTries(int numberOfTries) {
		this.numberOfFailedTries = numberOfTries;
	}

	public int getMinPasswordLength() {
		return minPasswordLength;
	}

	public void setMinPasswordLength(int minPasswordLength) {
		this.minPasswordLength = minPasswordLength;
	}

	public int getMinUsernameLength() {
		return minUsernameLength;
	}

	public void setMinUsernameLength(int minUsernameLenght) {
		this.minUsernameLength = minUsernameLenght;
	}

	public PasswordStrength getPasswordStrength() {
		return passwordStrength;
	}

	public void setPasswordStrength(PasswordStrength passwordStrength) {
		this.passwordStrength = passwordStrength;
	}

	public int getWaitTimeForRelogin() {
		return waitTimeForRelogin;
	}

	public void setWaitTimeForRelogin(int waitTimeForRelogin) {
		this.waitTimeForRelogin = waitTimeForRelogin;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	private static class PasswordMessageResolver extends MessageResolver {

		public PasswordMessageResolver() {
			super(new Properties());
		}

		@Override
		public String resolve(final RuleResultDetail detail) {
			final String key = "security.error." + detail.getErrorCode();
			return String.format(Locale.getInstance().getMessages().get(key), detail.getValues());
		}
	}

	public void validatePassword(String username, String password) throws ValidationException, IOException {
		List<Rule> rules = getRulesList();
		PasswordValidator validator = new PasswordValidator(new PasswordMessageResolver(), rules);
		PasswordData passwordData = new PasswordData(new edu.vt.middleware.password.Password(password));
		passwordData.setUsername(username);
		RuleResult result = validator.validate(passwordData);
		if (!result.isValid()) {
			List<UserMessage> messages = new ArrayList<UserMessage>();
			for (String msg : validator.getMessages(result)) {
				messages.add(new UserMessage(UserMessageSeverity.ERROR, msg, null));
			}
			throw new ValidationException(messages);
		}
	}

	public String generateValidatablePassword() {
		PasswordGenerator generator = new PasswordGenerator();
		List<CharacterRule> rules = new ArrayList<CharacterRule>();
		if (getPasswordStrength() != PasswordStrength.LOW) {
			if (getPasswordStrength() != PasswordStrength.MEDIUM) {
				rules.add(new DigitCharacterRule(1));
				rules.add(new NonAlphanumericCharacterRule(1));
				rules.add(new UppercaseCharacterRule(1));
				rules.add(new LowercaseCharacterRule(1));
			}
		}
		return generator.generatePassword(getMinPasswordLength(), rules);
	}

	protected List<Rule> getRulesList() {
		List<Rule> ruleList = new ArrayList<>();

		ruleList.add(new LengthRule(getMinPasswordLength(), 200));

		if (getPasswordStrength() != PasswordStrength.LOW) {
			// don't allow alphabetical sequences
			ruleList.add(new AlphabeticalSequenceRule(4, false));

			// don't allow numerical sequences of length 3
			ruleList.add(new NumericalSequenceRule(4, false));

			// don't allow qwerty sequences
			ruleList.add(new QwertySequenceRule());

			// don't allow 4 repeat characters
			ruleList.add(new RepeatCharacterRegexRule(4));

			ruleList.add(new UsernameRule());

			if (getPasswordStrength() != PasswordStrength.MEDIUM) {
				// control allowed characters
				CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
				// require at least 1 digit in passwords
				charRule.getRules().add(new DigitCharacterRule(1));
				// require at least 1 non-alphanumeric char
				charRule.getRules().add(new NonAlphanumericCharacterRule(1));
				// require at least 1 upper case char
				charRule.getRules().add(new UppercaseCharacterRule(1));
				// require at least 1 lower case char
				charRule.getRules().add(new LowercaseCharacterRule(1));
				// require at least 3 of the previous rules be met
				charRule.setNumberOfCharacteristics(3);
				ruleList.add(charRule);
			}
		}
		return ruleList;
	}

}
