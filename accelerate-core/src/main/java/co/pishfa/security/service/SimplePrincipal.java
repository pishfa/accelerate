/**
 * 
 */
package co.pishfa.security.service;

import java.security.Principal;

/**
 * @author Taha Ghasemi
 * 
 */
public class SimplePrincipal implements Principal {

	private String name;

	/**
	 * @param name
	 */
	public SimplePrincipal(String name) {
		super();
		this.name = name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
