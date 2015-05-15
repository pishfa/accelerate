package co.pishfa.accelerate.persistence.filter;

import co.pishfa.accelerate.persistence.query.QueryBuilder;

import java.util.List;

/**
 * A filter that its fields can be defined dynamically.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DynamicFilter<E> extends SimpleFilter<E> {

	private final Object[] values;
	private final FilterFieldMetadata[] fields;

	@SafeVarargs
	public DynamicFilter(String viewAction, FilterFieldMetadata... fields) {
		super(viewAction);
		values = new Object[fields.length];
		this.fields = fields;
	}

	@Override
	public void addConditions(QueryBuilder<E> query) {
		super.addConditions(query);
		for (int i = 0; i < values.length; i++) {
			FilterFieldMetadata filterField = fields[i];
			FilterFieldCondition condition = filterField.getCondition();
			Object value = values[i];
			if (!filterField.getType().isClean(value) && condition != FilterFieldCondition.NONE) {
				if (value instanceof List) {
                    if(!((List<?>) value).isEmpty()) {
                        // multiple values are OR together
                        int j = 0;
                        query.append(" and (");
                        for (Object v : ((List<?>) value)) {
                            if (j > 0)
                                query.append(" OR ");
                            String param = "param" + String.valueOf(i) + String.valueOf(j++);
                            condition.addTo(query, filterField.getPath(), param, v);
                        }
                        query.append(")");
                    }
				} else {
					String param = "param" + String.valueOf(i);
					query.append(" and ");
					condition.addTo(query, filterField.getPath(), param, value);
				}
			}
		}
	}

	@Override
	public void clean() {
		super.clean();
		for (int i = 0; i < values.length; i++) {
            if(fields[i].isCleanable())
			    values[i] = null;
		}
	}

	@Override
	public boolean isClean() {
		for (int i = 0; i < values.length; i++) {
			if (fields[i].isCleanable() && !fields[i].getType().isClean(values[i])) {
				return false;
			}
		}
		return true;
	}

	public Object[] getValues() {
		return values;
	}

	public FilterFieldMetadata[] getFields() {
		return fields;
	}

}
