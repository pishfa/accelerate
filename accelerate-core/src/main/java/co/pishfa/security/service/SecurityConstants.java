/**
 * 
 */
package co.pishfa.security.service;

/**
 * @author Taha Ghasemi
 * 
 */
public interface SecurityConstants {

	/**
	 * Session attribute
	 */
	String SESSION_LOCAL_LOGIN = "security.localLogin";

	/**
	 * Session attribute
	 */
	String SESSION_IDENTITY = "security.identity";

	/**
	 * 
	 */
	String ACTION_USER_LOGIN_MORE_THAN_ALLOWED = "user.login.moreThanAllowed";

	/**
	 * 
	 */
	String ACTION_USER_LOGIN = "user.login";

	/**
	 * 
	 */
	String ACTION_USER_LOGOUT = "user.logout";

	/**
	 * 
	 */
	String ACTION_RUN_AS = "security.runAs";

}
