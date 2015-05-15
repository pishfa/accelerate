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
	public static final String SESSION_LOCAL_LOGIN = "security.localLogin";

	/**
	 * Session attribute
	 */
	public static final String SESSION_IDENTITY = "security.identity";

	/**
	 * 
	 */
	public static final String ACTION_USER_LOGIN_MORE_THAN_ALLOWED = "user.loginMoreThanAllowed";

	/**
	 * 
	 */
	public static final String ACTION_USER_LOGIN = "user.login";

	/**
	 * 
	 */
	public static final String ACTION_USER_LOGOUT = "user.logout";

	/**
	 * 
	 */
	public static final String ACTION_RUN_AS = "security.runAs";

}
