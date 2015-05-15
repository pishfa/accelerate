/**
 * 
 */
package co.pishfa.accelerate.ui.param;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;

import co.pishfa.accelerate.convert.Converter;
import co.pishfa.accelerate.ui.UiUtils;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * @author Taha Ghasemi
 * 
 */
@UiParamGetter
@Interceptor
public class UiParamGetterInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Converter converter;

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Method method = ic.getMethod();
		String paramName = getParamName(method);
		String value = UiUtils.getRequest().getParameter(paramName);
		try {
			return converter.toObject(value, method.getReturnType());
		} catch (Exception e) {
			return null;
		}
	}

	public String getParamName(Method method) {
		String annotatedParamName = method.getAnnotation(UiParamGetter.class).value();
		if (StrUtils.isEmpty(annotatedParamName)) {
			return StringUtils.removeStart(method.getName(), "getParam");
		} else {
			return annotatedParamName;
		}
	}
}
