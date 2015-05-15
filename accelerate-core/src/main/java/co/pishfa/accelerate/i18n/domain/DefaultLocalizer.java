/**
 * 
 */
package co.pishfa.accelerate.i18n.domain;

import co.pishfa.accelerate.i18n.model.LocaleDate;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.TimeZone;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DefaultLocalizer implements Localizer {

	protected final Locale locale;

	public DefaultLocalizer(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String convert(String str) {
		return str;
	}

	/**
	 * Get the date based on the current locale
	 */
	@Override
	public String getDate(Date d, int style, TimeZone timeZone) {
		// Note: Date format is not thread-safe
		return DateFormat.getDateInstance(getCalendar(timeZone), DateFormat.DEFAULT, locale).format(d);
	}

	@Override
	public String getDateTime(Date d, int style, TimeZone timeZone) {
		return DateFormat.getDateTimeInstance(getCalendar(timeZone), DateFormat.DEFAULT, DateFormat.DEFAULT, locale)
				.format(d);
	}

	@Override
	public String getTime(Date d, int style, TimeZone timeZone) {
		// Note: Date format is not thread-safe
		return DateFormat.getTimeInstance(getCalendar(timeZone), DateFormat.DEFAULT, locale).format(d);
	}

	@Override
	public boolean isRTL() {
		return false;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public LocaleDate convert(Date d, TimeZone timeZone) {
		com.ibm.icu.util.Calendar calendar = getCalendar(timeZone);
		calendar.setTime(d);
		return new LocaleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public String getDate(LocaleDate d, int style, TimeZone timeZone) {
		// TODO
		int year = d.getYear();
		if (style == -1)
			year = year % 100;
		return new StringBuilder().append(year).append('/').append(d.getMonth()).append('/').append(d.getDay())
				.toString();
	}

	@Override
	public String getCurrency(long m, int style) {
		return com.ibm.icu.text.NumberFormat.getCurrencyInstance(locale).format(m);
	}

	@Override
	public Date convert(LocaleDate d, TimeZone timeZone) {
		com.ibm.icu.util.Calendar c = getCalendar(timeZone);
		c.set(Calendar.YEAR, d.getYear());
		c.set(Calendar.MONTH, d.getMonth() - 1);
		c.set(Calendar.DATE, d.getDay());
		return c.getTime();
	}

	@Override
	public com.ibm.icu.util.Calendar getCalendar(TimeZone timeZone) {
		return com.ibm.icu.util.Calendar.getInstance(timeZone, locale);
	}

	final static String[] units = new String[] { " B", " KB", " MB", " GB", " TB" };

	@Override
	public String getTimeDifference(final Date a, final Date b) {
        if(a == null || b == null)
            return null;
		long time = a.getTime() > b.getTime() ? a.getTime() - b.getTime() : b.getTime() - a.getTime();
		int seconds = (int) ((time / 1000) % 60);
		int minutes = (int) ((time / 60000) % 60);
		int hours = (int) ((time / 3600000) % 24);
		String secondsStr = (seconds < 10 ? "0" : "") + seconds;
		String minutesStr = (minutes < 10 ? "0" : "") + minutes;
		String hoursStr = (hours < 10 ? "0" : "") + hours;
		return hoursStr + ":" + minutesStr + ":" + secondsStr;
	}

    public String getDateDifference(final Date a, final Date b) {
       return null;
    }

	@Override
	public String getDisplaySize(long size) {
		if (size <= 0)
			return "0";
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
	}

	@Override
	public String getDisplaySpeed(float bytePerSecond) {
		return getDisplaySize((long) bytePerSecond) + "/s";
	}

    @Override
    public String getPercent(float percent) {
        return NumberFormat.getPercentInstance(locale).format(percent);
    }
}
