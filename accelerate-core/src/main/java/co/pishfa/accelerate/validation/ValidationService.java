/**
 * 
 */
package co.pishfa.accelerate.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.service.Service;

/**
 * @author Taha Ghasemi
 * 
 */
@Service
public class ValidationService {

	private Validator validator;

	public static ValidationService getInstance() {
		return CdiUtils.getInstance(ValidationService.class);
	}

	/**
	 * @return the validator
	 */
	@Produces
	@ApplicationScoped
	public Validator getValidator() {
		if (validator == null) {
			ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
			/*ValidatorContext validatorContext = validatorFactory.usingContext();
			validatorContext.messageInterpolator(new LocalizedMessageInterpolator(validatorFactory
					.getMessageInterpolator()));*/
			validator = validatorFactory.getValidator();
		}
		return validator;
	}

}
