/**
 * 
 */
package co.pishfa.accelerate.i18n.model;

import co.pishfa.accelerate.reflection.PropertyAccessor;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * Can be used to eliminate the creation of MultiStringFaLong to avoid Null access in JSF even in case of optional fields. This proxy, defers
 * the creation of the field until it is actually needed. This saves the database from dummy records.
 * 
 * @author Taha Ghasemi
 * 
 */
public class MultiStringLongProxy extends PropertyAccessor<MultiStringFaLong> {

	public MultiStringLongProxy(String propertyName, Object entity) {
		super(propertyName, entity);
	}

	public String getFa() {
		MultiStringFaLong target = getTarget();
		return target == null ? null : target.getFa();
	}

	public String getEn() {
		MultiStringFaLong target = getTarget();
		return target == null ? null : target.getEn();
	}

	public void setFa(String str) {
		MultiStringFaLong target = getTarget();
		if (target == null) {
			if (StrUtils.isEmpty(str)) {
				return;
			} else {
				target = new MultiStringFaLong();
				setTarget(target);
			}
		}
		target.setFa(str);
	}

	public void setEn(String str) {
		MultiStringFaLong target = getTarget();
		if (target == null) {
			if (StrUtils.isEmpty(str)) {
				return;
			} else {
				target = new MultiStringFaLong();
				setTarget(target);
			}
		}
		target.setEn(str);
	}

}
