package co.pishfa.accelerate.clone;

/**
 * @author Taha Ghasemi.
 */
public interface CloneProcessor {

    <T> T preClone(T o);
    <T> void postClone(T o, Class<T> clz);
    <T> T init(Class<T> clz);
}
