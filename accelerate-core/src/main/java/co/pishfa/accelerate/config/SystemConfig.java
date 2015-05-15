/**
 * 
 */
package co.pishfa.accelerate.config;

import co.pishfa.accelerate.utility.CommonUtils;

/**
 * A configuration that relates to the system properties.
 * 
 * @author Taha Ghasemi
 * 
 */
public class SystemConfig extends AbstractConfig {

	@Override
	public <T> T getObject(String key) {
		return CommonUtils.cast(System.getProperty(key));
	}

	@Override
	public void setObject(String key, Object value) {
		System.setProperty(key, String.valueOf(value));
	}

}
