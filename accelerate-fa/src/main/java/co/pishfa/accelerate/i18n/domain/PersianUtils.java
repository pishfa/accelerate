package co.pishfa.accelerate.i18n.domain;

/**
 * Common utility functions relating to Strings
 * 
 * @author Taha Ghasemi
 * 
 */
public final class PersianUtils {

	// private static final String NUM_EN = "0123456789";
	private static final String NUM_REPLACE = "" + '\u06F0' + '\u06F1' + '\u06F2' + '\u06F3' + '\u06F4' + '\u06F5'
			+ '\u06F6' + '\u06F7' + '\u06F8' + '\u06F9';
	private static final String FA_CHARS = "" + '\u06CC' + '\u06A9';
	private static final String AR_CHARS = "" + '\u064A' + '\u0643';
	// gach pazh
	private static final String FA_NON_ARABIC = "" + '\u06AF' + '\u0686' + '\u067E' + '\u0698';
	// keh jim beh zeh
	private static final String FA_NON_ARABIC_TO_ARABIC = "" + '\u0643' + '\u062C' + '\u0628' + '\u0632';

	public static String convertNumbers(String str) {
		// return StringUtils.replaceChars(str, NUM_EN, NUM_REPLACE);
		if (isEmpty(str)) {
			return str;
		}

		StringBuffer buf = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			int offset = (ch) - '0';
			if (offset < 10 && offset >= 0) {
				ch = NUM_REPLACE.charAt(offset);
			}
			buf.append(ch);
		}
		return buf.toString();
	}

	public static String convertToPersian(String str) {
		return org.apache.commons.lang3.StringUtils.replaceChars(str, AR_CHARS, FA_CHARS);
	}

	public static String convertForSort(String str) {
		if (isEmpty(str)) {
			return str;
		}

		StringBuffer buf = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			int code = ch;
			if (code > 0x0620) { // arabic range
				int ind = FA_NON_ARABIC.indexOf(ch);
				if (ind >= 0) {
					code = FA_NON_ARABIC_TO_ARABIC.charAt(ind);
				}
				// make room for persian charachters between arabic ones
				code = code * 2 - 0x0620;
				if (ind >= 0) {
					code++;
				}
				ch = (char) code;
			}
			buf.append(ch);
		}
		return buf.toString();
	}

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

}
