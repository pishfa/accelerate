/**
 * 
 */
package co.pishfa.security.service;

import co.pishfa.accelerate.config.ConfigEntity;
import co.pishfa.accelerate.config.cdi.ConfigGetter;
import co.pishfa.accelerate.core.ConfigAppliedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 * 
 */
@ApplicationScoped
@ConfigEntity("security")
public class SecurityConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ConfigEntity("onlineUser")
    public static class OnlineUserConfig {
        private int updateInterval;

        public int getUpdateInterval() {
            return updateInterval;
        }

        public void setUpdateInterval(int updateInterval) {
            this.updateInterval = updateInterval;
        }

    }

	private static final String ENABLED = "security.enabled";

    //We cache it for fast access
	private boolean securityEnabled;

	public void onChange(@Observes ConfigAppliedEvent event) {
		securityEnabled = event.getConfig().getBoolean(ENABLED);
	}

	@ConfigGetter
    public AuditConfig getAuditConfig() {
        return null;
    }

    @ConfigGetter
	public OnlineUserConfig getOnlineUserConfig() {
		return null;
	}

    @ConfigGetter("sso")
	public boolean isSsoEnabled() {
		return false;
	}

	public boolean isSecurityEnabled() {
		return securityEnabled;
	}

}
