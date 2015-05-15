/**
 * 
 */
package co.pishfa.security.entity.authorization;

/**
 * @author Taha Ghasemi
 * 
 */
public enum AccessRuleType {

	ALLOW(false), DENY(false), VETO_ALLOW(true), VETO_DENY(true);

	private boolean veto;

	/**
	 * @return the veto
	 */
	public boolean isVeto() {
		return veto;
	}

	private AccessRuleType(boolean veto) {
		this.veto = veto;
	}

	/**
	 * @return
	 */
	public boolean isAllow() {
		return this == ALLOW || this == VETO_ALLOW;
	}

}
