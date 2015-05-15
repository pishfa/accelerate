package co.pishfa.accelerate.utility;

import co.pishfa.accelerate.clone.Cloner;

/**
 * Common utility functions
 * 
 * @author Taha Ghasemi
 * 
 */
public final class CommonUtils {

	// private static final Logger log = LoggerFactory.getLogger(CommonUtilities.class);

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return obj == null ? null : (T) obj;
	}

	public static <T> T copy(T obj) {
		return (new Cloner()).deepClone(obj);
	}

    public static int toInt(Object o) {
        return toInt(o, 0);
    }

    public static int toInt(Object o, int def) {
        return o == null? def : (Integer) o;
    }

    public static long toLong(Object o) {
        return toLong(o, 0);
    }

    public static long toLong(Object o, long def) {
        return o == null? def : (Long) o;
    }

    public static boolean toBoolean(Object o) {
        return toBoolean(o, false);
    }

    public static boolean toBoolean(Object o, boolean def) {
        return o == null? def: (Boolean) o;
    }

}
