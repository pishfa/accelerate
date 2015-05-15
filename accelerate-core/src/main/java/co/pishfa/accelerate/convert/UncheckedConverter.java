package co.pishfa.accelerate.convert;

/**
 * This class, does not throws any exceptions in case of conversion errors.
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class UncheckedConverter extends AbstractConverter {

	private static final long serialVersionUID = 1L;

	private final Converter delegate;

	public UncheckedConverter(Converter delegate) {
		this.delegate = delegate;
	}

	@Override
	public <T> T toObject(Object value, Class<T> type) {
		try {
			return delegate.toObject(value, type);
		} catch (Exception e) {
			return null;
		}
	}

}
