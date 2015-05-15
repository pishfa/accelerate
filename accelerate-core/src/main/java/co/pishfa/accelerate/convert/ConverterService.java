package co.pishfa.accelerate.convert;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import co.pishfa.accelerate.cdi.CdiUtils;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@ApplicationScoped
public class ConverterService {

	private Converter converter;
	private Converter uncheckedConverter;

	public static ConverterService getInstance() {
		return CdiUtils.getInstance(ConverterService.class);
	}

	@Produces
	@ApplicationScoped
	public Converter getConverter() {
		if (converter == null) {
			converter = new DefaultConverter();
		}
		return converter;
	}

	@Produces
	@ApplicationScoped
	@Unchecked
	public Converter getUncheckedConverter() {
		if (uncheckedConverter == null) {
			uncheckedConverter = new UncheckedConverter(getConverter());
		}
		return uncheckedConverter;
	}

}
