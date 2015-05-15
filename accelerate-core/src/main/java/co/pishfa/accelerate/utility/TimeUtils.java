/**
 * 
 */
package co.pishfa.accelerate.utility;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Taha Ghasemi
 * 
 */
public class TimeUtils {

	public static long toMilliSecond(final long duration, final TimeUnit unit) {
		return TimeUnit.MILLISECONDS.convert(duration, unit);
	}

	public static Date toDate(final long time) {
		return new Date(time);
	}

	public static Date now() {
		return new Date();
	}

	public static long since(final long time) {
		return System.currentTimeMillis() - time;
	}

}
