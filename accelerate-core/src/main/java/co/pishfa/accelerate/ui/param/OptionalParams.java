package co.pishfa.accelerate.ui.param;

import co.pishfa.accelerate.ui.UiUtils;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class OptionalParams {

	public static Integer getInt(String name) {
		String value = getString(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Boolean getBoolean(String name) {
		String value = getString(name);
		if (value != null) {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		Boolean res = getBoolean(name);
		return res != null ? res : defaultValue;
	}

	public static Long getLong(String name) {
		String value = getString(name);
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static String getString(String name) {
		return getString(name, null);
	}

	public static String getString(String name, String defaultValue) {
		String value = UiUtils.getRequest().getParameter(name);
		return StrUtils.defaultIfNull(value, defaultValue);
	}

}
