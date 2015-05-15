package co.pishfa.accelerate.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.pishfa.accelerate.cdi.Veto;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Veto
public abstract class AbstractConverter implements Converter {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString(Object value) {
		return toObject(value, String.class);
	}

	@Override
	public Integer toInteger(Object value) {
		return toObject(value, Integer.class);
	}

	@Override
	public Long toLong(Object value) {
		return toObject(value, Long.class);
	}

	@Override
	public Float toFloat(Object value) {
		return toObject(value, Float.class);
	}

	@Override
	public Boolean toBoolean(Object value) {
		return toObject(value, Boolean.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(Object[] values, Class<T> type) {
		if (values == null) {
			return null;
		}

		T[] arr = (T[]) new Object[values.length];
		for (int i = 0; i < values.length; i++) {
			arr[i] = toObject(values[i], type);
		}
		return arr;
	}

	@Override
	public <T> List<T> toList(Collection<Object> values, Class<T> type) {
		if (values == null) {
			return null;
		}

		List<T> list = new ArrayList<>(values.size());
		for (Object value : values) {
			list.add(toObject(value, type));
		}
		return list;
	}

	@Override
	public <T> List<T> toList(Object[] values, Class<T> type) {
		if (values == null) {
			return null;
		}

		List<T> list = new ArrayList<>(values.length);
		for (Object value : values) {
			list.add(toObject(value, type));
		}
		return list;
	}

	@Override
	public <T extends Enum<T>> T toEnum(Object value, Class<T> type) {
		return Enum.valueOf(type, toString(value));
	}

}