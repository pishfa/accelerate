package co.pishfa.accelerate.persistence.filter;

import org.apache.commons.lang3.Validate;

import co.pishfa.accelerate.persistence.query.QueryBuilder;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public enum FilterFieldCondition {
	STARTS_WITH(" like "),
	ENDS_WITH(" like "),
	CONTAINS(" like "),
	GTE(" >= "),
	LTE(" <= "),
	GT(" > "),
	LT(" < "),
	EQUALS(" = "),
	NOT_EQUALS(" <> "),
	IN(" in "),
	NOT_IN(" not in "),
	BETWEEN(" between "),
	DESCENDANTS(" member of "),
	MEMBER_OF(" member of "),
	NONE("");

	private String function;

	private FilterFieldCondition(String function) {
		this.function = function;
	}

	public <E> void addTo(QueryBuilder<E> query, String field, String param, Object value) {
		if (this == BETWEEN) {
			Validate.isInstanceOf(FilterInterval.class, value);
			FilterInterval interval = (FilterInterval) value;
			Object start = interval.getIntervalStart();
			Object end = interval.getIntervalEnd();
			if (start != null && end != null) {
				query.append(field).append(function);
				String param1 = param + "1";
				query.append(":").append(param1).with(param1, start);
				query.append(" and ");
				String param2 = param + "2";
				query.append(":").append(param2).with(param2, end);
			} else if (start != null) {
				query.append(field).append(" >= :").append(param).with(param, start);
			} else {
				query.append(field).append(" <= :").append(param).with(param, end);
			}

		} else if (this == DESCENDANTS) {
			query.append("(:").append(param).append(function).append(field).append(".").append("parents OR ")
					.append(field).append(" = :").append(param).append(")").with(param, value);
		} else {
			query.append(field).append(function).append(":").append(param).with(param, getArgument(value));
		}
	}

	public String getFunction() {
		return function;
	}

	public Object getArgument(Object value) {
		switch (this) {
		case STARTS_WITH:
			return value + "%";
		case ENDS_WITH:
			return "%" + value;
		case CONTAINS:
			return "%" + value + "%";
		default:
			return value;
		}
	}
}
