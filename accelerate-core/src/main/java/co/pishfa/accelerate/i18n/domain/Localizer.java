/**
 * 
 */
package co.pishfa.accelerate.i18n.domain;

import co.pishfa.accelerate.i18n.model.LocaleDate;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

import java.util.Date;
import java.util.Locale;

/**
 * Localization methods for specific locale but with variable time zone and date/time/currency formats.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public interface Localizer {

	String convert(String str);

	LocaleDate convert(Date d, TimeZone timeZone);

	Date convert(LocaleDate d, TimeZone timeZone);

	boolean isRTL();

	Locale getLocale();

	Calendar getCalendar(TimeZone timeZone);

	String getDate(Date d, int style, TimeZone timeZone);

	String getDate(LocaleDate d, int style, TimeZone timeZone);

	String getDateTime(Date d, int style, TimeZone timeZone);

	String getTime(Date d, int style, TimeZone timeZone);

	String getCurrency(long m, int style);

	String getTimeDifference(final Date a, final Date b);

    String getDateDifference(final Date a, final Date b);

	String getDisplaySize(long size);

	String getDisplaySpeed(float bytePerSecond);

    String getPercent(float percent);
}
