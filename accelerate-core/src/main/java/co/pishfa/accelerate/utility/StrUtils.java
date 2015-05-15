package co.pishfa.accelerate.utility;

/**
 * Common utility functions relating to Strings
 * 
 * @author Taha Ghasemi
 * 
 */
public final class StrUtils {

	/**
	 * Checks whether given name is contained in the names
	 * 
	 * @param name
	 * @param names
	 * @return name if exists, null otherwise
	 */
	public static String check(String name, String[] names) {
		if (name == null || names == null) {
			return null;
		}
		for (String name2 : names) {
			if (name.equals(name2)) {
				return name;
			}
		}
		return null;
	}

	/**
	 * oracle treats empty strings as nulls
	 * 
	 * @param str
	 * @return
	 */
	public static String nullToEmpty(String str) {
		if (isEmpty(str)) {
			return "";
		}
		return str;
	}

	public static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}

	public static String defaultIfEmpty(String value, String defaultValue) {
		return isEmpty(value) ? defaultValue : value;
	}

	public static String defaultIfNull(String value, String defaultValue) {
		return value == null ? defaultValue : value;
	}

	public static String concatNonEmpty(String delimiter, String... strings) {
		boolean first = true;
		StringBuilder res = new StringBuilder();
		for (String str : strings) {
			if (isEmpty(str)) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				res.append(delimiter);
			}
			res.append(str);
		}
		return res.toString();
	}

}
