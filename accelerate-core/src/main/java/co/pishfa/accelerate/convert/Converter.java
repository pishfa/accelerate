/**
 * 
 */
package co.pishfa.accelerate.convert;

import co.pishfa.accelerate.cdi.Veto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Returns the actual value after conversions and castings. Methods should throw exception upon the conversion failure.
 * In case of provided value is null, null should be returned.
 * 
 * @author Taha Ghasemi
 * 
 */
@Veto
public interface Converter extends Serializable {

	public String toString(Object value);

	public Integer toInteger(Object value);

	public Long toLong(Object value);

	public Float toFloat(Object value);

	public Boolean toBoolean(Object value);

	public <T extends Enum<T>> T toEnum(Object value, Class<T> type);

	public <T> T toObject(Object value, Class<T> type);

	public <T> T[] toArray(Object[] value, Class<T> type);

	public <T> List<T> toList(Object[] value, Class<T> type);

	public <T> List<T> toList(Collection<Object> value, Class<T> type);

}
