package co.pishfa.accelerate.persistence.filter;

import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.utility.StrUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class TypedFilterMetadata<E> {

	public static class TypedFilterFieldMetadata extends FilterFieldMetadata {
		private Field field;

		public TypedFilterFieldMetadata(FilterFieldType type, String name, FilterFieldCondition condition, Field field, boolean cleanable) {
			super(type, name, condition, cleanable);
			this.field = field;
		}

		public Field getField() {
			return field;
		}

        public void setField(Field field) {
            this.field = field;
        }
    }

	private final Map<String, TypedFilterFieldMetadata> filterFields = new HashMap();

    public Map<String, TypedFilterFieldMetadata> getFilterFields() {
        return filterFields;
    }

    public TypedFilterMetadata(Class<?> filterClass) {
		computeMetadata(filterClass);
	}

	private TypedFilterFieldMetadata extractMetadata(Field field, FilterField filterField) {
		String name;
		if (StrUtils.isEmpty(filterField.expr())) {
			name = "e." + field.getName();
		} else {
			name = filterField.expr();
		}

		FilterFieldType type = field.getType().isAssignableFrom(CharSequence.class) ? FilterFieldType.STRING
				: FilterFieldType.OBJECT;

		return new TypedFilterFieldMetadata(type, name, filterField.condition(), field, filterField.cleanable() && !field.getType().isPrimitive());
	}

	public void computeMetadata(Class<?> filterClass) {
        if(filterClass == Object.class)
            return;
		for (Field field : filterClass.getDeclaredFields()) {
			FilterField filterField = field.getAnnotation(FilterField.class);
			if (filterField != null) {
				addFilterField(field, filterField);
			}
		}
        computeMetadata(filterClass.getSuperclass());
	}

	public void addFilterField(Field field, FilterField filterField) {
		Validate.notNull(field);
		Validate.notNull(filterField);

		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
        TypedFilterFieldMetadata metadata = extractMetadata(field, filterField);
        filterFields.put(metadata.getPath(), metadata);
	}

	public void addConditions(QueryBuilder<E> query, Object filterObj) throws IllegalArgumentException,
			IllegalAccessException {
        int i=0;
		for (TypedFilterFieldMetadata filterField : filterFields.values()) {
			Object value = filterField.getField().get(filterObj);
			FilterFieldCondition condition = filterField.getCondition();
			if (!filterField.getType().isClean(value) && condition != FilterFieldCondition.NONE) {
				if (value instanceof List) {
					// multiple values are OR together
                    if(!((List<?>) value).isEmpty()) {
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
            i++;
		}
	}

	public void clean(Object filterObj) throws IllegalArgumentException, IllegalAccessException {
		for (TypedFilterFieldMetadata filterField : filterFields.values()) {
            if(filterField.isCleanable())
			    filterField.getField().set(filterObj, null);
		}
	}

	public boolean isClean(Object filterObj) throws IllegalArgumentException, IllegalAccessException {
		for (TypedFilterFieldMetadata filterField : filterFields.values()) {
			Object value = filterField.getField().get(filterObj);
			if (filterField.isCleanable() && !filterField.getType().isClean(value)) {
				return false;
			}
		}
		return true;
	}

}
