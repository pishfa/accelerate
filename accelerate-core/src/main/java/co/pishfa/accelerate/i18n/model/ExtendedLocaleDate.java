package co.pishfa.accelerate.i18n.model;

import co.pishfa.accelerate.i18n.domain.Locale;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * It contains both the gregorian, and its corresponding components in a locale.. The gregorian date is the reference
 * and the locale values are computed from it accordingly.
 * 
 * @author Taha Ghasemi
 * 
 */
// NOTE: we repeat the fields from the parent, since some JPA providers doesn't support inheritance among embedables.
@Embeddable
public class ExtendedLocaleDate extends LocaleDate {

	private static final long serialVersionUID = 1L;

	private Integer year;
	private Byte month;
	private Byte day;

	@Temporal(TemporalType.DATE)
	private Date date;

	public ExtendedLocaleDate() {
	}

	public ExtendedLocaleDate(Date d) {
        setDate(d);
	}

	public ExtendedLocaleDate(Date d, Locale locale) {
        setDate(d, locale);
	}

    public ExtendedLocaleDate(int year, byte month, byte day, Date date) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.date = date;
    }

    @Override
	public Date getDate() {
		return date;
	}

    @Override
	protected void copyFrom(LocaleDate l) {
		this.day = (byte) l.getDay();
		this.month = (byte) l.getMonth();
		this.year = l.getYear();
	}

	public void setDate(Date d) {
		setDate(d, Locale.getInstance());
	}

	public void setDate(Date d, Locale locale) {
		this.date = d;
		copyFrom(locale.convert(d));
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public void setYear(int year) {
		this.year = year;
		this.date = getDate();
	}

	@Override
	public int getMonth() {
		return month;
	}

	@Override
	public void setMonth(int month) {
		this.month = (byte) month;
		this.date = getDate();
	}

	@Override
	public int getDay() {
		return day;
	}

	@Override
	public void setDay(int day) {
		this.day = (byte) day;
		this.date = getDate();
	}

	@Override
	public ExtendedLocaleDate clone() {
		return (ExtendedLocaleDate) super.clone();
	}

	@Override
	public String toString() {
		return "ExtendedLocaleDate [year=" + year + ", month=" + month + ", day=" + day + ", date=" + date + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedLocaleDate other = (ExtendedLocaleDate) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}

}
