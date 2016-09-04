/**
 * 
 */
package co.pishfa.accelerate.i18n.domain;

import com.ghasemkiani.util.icu.PersianCalendar;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class FaLocalizer extends DefaultLocalizer {

	private static final Locale locale = new Locale("fa");

	public FaLocalizer() {
		super(locale);
	}

	@Override
	public String convert(String str) {
		return PersianUtils.convertToPersian(str);
	}

	/**
	 * Get the date based on the current locale
	 */
	@Override
	public String getDate(Date d, int style, TimeZone timeZone) {
		return getDate(convert(d, timeZone), style, timeZone);
	}

	@Override
	public String getDateTime(Date d, int style, TimeZone timeZone) {
		return getDate(d, style, timeZone) + "-" + getTime(d, style, timeZone);
	}

	@Override
	public String getTime(Date d, int style, TimeZone timeZone) {
		return PersianUtils.convertNumbers(super.getTime(d, style, timeZone));
	}

	@Override
	public boolean isRTL() {
		return true;
	}

	@Override
	public String getCurrency(long m, int style) {
		return new DecimalFormat("#,###").format(m);
	}

	@Override
	public Calendar getCalendar(TimeZone timeZone) {
		return new PersianCalendar(timeZone, locale);
	}

    public String getDateDifference(final Date a, final Date b) {
        PersianCalendar ac = new PersianCalendar(a);
        PersianCalendar bc = new PersianCalendar(b);

        long at = ac.get(Calendar.YEAR) * 365 + ac.get(Calendar.DAY_OF_YEAR);
        long bt = bc.get(Calendar.YEAR) * 365 + bc.get(Calendar.DAY_OF_YEAR);
        long diff = Math.abs(at - bt);

        if(diff >= 365) {
           return (diff / 365) + " سال " + (at < bt?"پیش":"بعد");
        } else {
            if(diff >= 30) {
                return (diff / 30) + " ماه " + (at < bt?"پیش":"بعد");
            } else {
                if(diff >= 7) {
                    return (diff / 7) + " هفته " + (at < bt?"پیش":"بعد");
                } else {
                    if(diff == 0) {
                        return "امروز";
                    } else if(diff == 1 && at < bt) {
                        return "دیروز";
                    } else if(diff == 1 && at > bt) {
                        return "فردا";
                    } else {
                        return (diff) + " روز " + (at < bt?"پیش":"بعد");
                    }
                }
            }
        }
    }

}
