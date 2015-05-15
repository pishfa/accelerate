/**
 * 
 */
package co.pishfa.accelerate.config;

import co.pishfa.accelerate.config.ConfigEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@ConfigEntity("security")
public class SecurityConfig {

	private boolean enabled;
	private boolean sso;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSso() {
		return sso;
	}

	public void setSso(boolean incremental) {
		this.sso = incremental;
	}

}
