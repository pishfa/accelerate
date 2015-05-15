/**
 * 
 */
package co.pishfa.accelerate.config.cdi;

import co.pishfa.accelerate.config.Config;
import co.pishfa.accelerate.config.ConfigEntity;
import co.pishfa.accelerate.reflection.ReflectionUtils;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @author Taha Ghasemi
 * 
 */
@ConfigGetter("")
@Interceptor
public class ConfigGetterInterceptor implements Serializable {

	@Inject
    @Global
	private Config config;

	private static final long serialVersionUID = 1L;

	public ConfigGetterInterceptor() {
	}

	@AroundInvoke
	public Object aroundInvoke(final InvocationContext ic) throws Exception {
		ConfigGetter configured = ic.getMethod().getAnnotation(ConfigGetter.class);
		String key = configured.value();
		Class<?> returnType = ic.getMethod().getReturnType();
        if(key.length()==0) {
            //Config entities do not require key
            if(returnType.isAnnotationPresent(ConfigEntity.class))
                return config.getObject(key, returnType);
            key = extractKey(ic.getMethod().getName());
        }
        //append alias of enclosing config entities, if any
        StringBuilder alias = new StringBuilder();
        ConfigService.appendEnclosingAlias(ic.getMethod().getDeclaringClass(), alias);
        return config.getObject(alias.append(key).toString(), returnType);
	}

    private String extractKey(String name) {
        return ReflectionUtils.getMethodPropertyName(name);
    }

}
