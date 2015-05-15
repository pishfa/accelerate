package co.pishfa.accelerate.convert;

/**
 * Converts one type of object to its string representation and vice versa.
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public interface ObjectConverter<T> {

    public String toString(T value);
    public T toObject(String value);


}
