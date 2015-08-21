/**
 * 
 */
package co.pishfa.accelerate.i18n.domain;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.i18n.model.LocaleDate;
import co.pishfa.accelerate.message.Messages;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.LoggedInEvent;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

/**
 * Holds localization information for the current user including its locale, messages, and time zone.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@SessionScoped
@Named
public class Locale implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private LocalizationService localizationService;

	private Messages messages;
	private boolean isRTL;
	private String lang;
	private Localizer localizer;
	private TimeZone timeZone;

	public static Locale getInstance() {
		return CdiUtils.getInstance(Locale.class);
	}

	@PostConstruct
	public void init() {
		setLocale(java.util.Locale.getDefault(), TimeZone.getDefault());
	}

	/**
	 * Loads user specific settings.
	 */
	public void onLoggin(@Observes LoggedInEvent event) throws Exception {
		String language = event.getUser().getLanguage();
		String country = event.getUser().getCountry();
		String timeZone = event.getUser().getTimeZone();
		setLocale(language, country, timeZone);
	}

	@Produces
	@Named("messages")
	@RequestScoped
	public Messages getMessages() {
		return messages;
	}

	public java.util.Locale getLocale() {
		return localizer.getLocale();
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @return the isRTL
	 */
	public boolean isRTL() {
		return isRTL;
	}

	public String getDir() {
		return isRTL ? "rtl" : "ltr";
	}

	/**
	 * Sets the locale and time zone based on the provided strings. This method will not change locale or time zone if
	 * their string representation are not provided.
	 */
	public void setLocale(String language, String country, String timeZone) {
		java.util.Locale locale = getLocale();
		if (!StrUtils.isEmpty(language)) {
			if (!StrUtils.isEmpty(country)) {
				locale = new java.util.Locale(language, country);
			} else
				locale = new java.util.Locale(language, country);
		}
		TimeZone zone = getTimeZone();
		if (!StrUtils.isEmpty(timeZone)) {
			zone = TimeZone.getTimeZone(timeZone);
		}
		setLocale(locale, zone);
	}

	public void setLocale(java.util.Locale locale, TimeZone timeZone) {
		messages = localizationService.getMessages(locale);
		localizer = localizationService.getLocalizer(locale);
		lang = locale.getLanguage();
		isRTL = localizer.isRTL();
		this.timeZone = timeZone;
	}

	public Localizer getLocalizer() {
		return localizer;
	}

	public String convert(String str) {
		return localizer.convert(str);
	}

	public LocaleDate convert(Date d) {
		return localizer.convert(d, timeZone);
	}

	public Date convert(LocaleDate d) {
		return localizer.convert(d, timeZone);
	}

	/**
	 * Get the date based on the current locale
	 */
	public String getDate(Date d) {
		if (d == null) {
			return "";
		}

		return localizer.getDate(d, -1, timeZone);
	}

	// jsf can not coerce correctly between date and localeDate so we rename the method
	public String getLocaleDate(LocaleDate d) {
		if (d == null) {
			return "";
		}

		return localizer.getDate(d, -1, timeZone);
	}

	public String getDateTime(Date d) {
		return getDate(d) + "-" + getTime(d);
	}

	public String getTime(Date d) {
		if (d == null) {
			return "";
		}

		return localizer.getTime(d, -1, timeZone);
	}

	public String getCurrency(long m) {
		return localizer.getCurrency(m, -1);
	}

	public Calendar getCalendar() {
		return localizer.getCalendar(timeZone);
	}

    public String getTimeDifference(final Date a) {
        return getTimeDifference(a, new Date());
    }

	public String getTimeDifference(final Date a, final Date b) {
		return localizer.getTimeDifference(a, b);
	}

    public String getDateDifference(final Date a) {
        return getDateDifference(a, new Date());
    }

    public String getDateDifference(final Date a, final Date b) {
        return localizer.getDateDifference(a,b);
    }

	public String getDisplaySize(long size) {
		return localizer.getDisplaySize(size);
	}

	public String getDisplaySpeed(float bytePerSecond) {
		return localizer.getDisplaySpeed(bytePerSecond);
	}

    public String getPercent(float percent) {
        return localizer.getPercent(percent);
    }

}
