/**
 * 
 */
package co.pishfa.accelerate.i18n.model;

import co.pishfa.accelerate.i18n.domain.Locale;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents the components of a date in a locale.
 * 
 * @author Taha Ghasemi
 * 
 */
@Embeddable
public class LocaleDate implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	private Integer year;
	private Byte month;
	private Byte day;

	public LocaleDate() {
	}

	public LocaleDate(Date d) {
		setDate(d);
	}

	public LocaleDate(Date d, Locale locale) {
		setDate(d, locale);
	}

	public LocaleDate(int year, int month, int day) {
		this.year = year;
		this.month = (byte) month;
		this.day = (byte) day;
	}

	public LocaleDate(LocaleDate d) {
		this(d.year, d.month, d.day);
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the 1-based month number.
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * 
	 * @param month
	 *            the 1-based month number.
	 */
	public void setMonth(int month) {
		this.month = (byte) month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = (byte) day;
	}

	protected void copyFrom(LocaleDate l) {
		this.day = l.day;
		this.month = l.month;
		this.year = l.year;
	}

	public Date getDate() {
		return Locale.getInstance().convert(this);
	}

	public void setDate(Date d) {
		setDate(d, Locale.getInstance());
	}

	public void setDate(Date d, Locale locale) {
		copyFrom(locale.convert(d));
	}

	@Override
	public LocaleDate clone() {
		try {
			return (LocaleDate) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	@Override
	public String toString() {
		return "LocaleDate [year=" + year + ", month=" + month + ", day=" + day + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocaleDate other = (LocaleDate) obj;
		if (day != other.day)
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

}
