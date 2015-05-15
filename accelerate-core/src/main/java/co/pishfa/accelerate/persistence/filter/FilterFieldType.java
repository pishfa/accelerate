package co.pishfa.accelerate.persistence.filter;

import co.pishfa.accelerate.utility.StrUtils;

public enum FilterFieldType {
	STRING,
	OBJECT;

	public boolean isClean(Object value) {
		switch (this) {
		case STRING:
			return StrUtils.isEmpty((String) value);
		case OBJECT:
			return value == null;
		}
		return true;
	}
}
