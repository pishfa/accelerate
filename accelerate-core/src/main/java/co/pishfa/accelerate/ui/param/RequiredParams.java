package co.pishfa.accelerate.ui.param;

import co.pishfa.accelerate.ui.UiUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class RequiredParams {

	public static int getInt(String name) {
		String value = getString(name);
		return Integer.parseInt(value);
	}

	public static boolean getBoolean(String name) {
		String value = getString(name);
		return Boolean.parseBoolean(value);
	}

	public static long getLong(String name) {
		String value = getString(name);
		return Long.parseLong(value);
	}

	public static String getString(String name) {
		String value = UiUtils.getRequest().getParameter(name);
		if (value == null) {
			throw new IllegalStateException("Param " + name + " is required");
		}
		return value;
	}

}
