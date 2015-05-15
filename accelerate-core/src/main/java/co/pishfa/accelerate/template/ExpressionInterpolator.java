package co.pishfa.accelerate.template;

import co.pishfa.accelerate.initializer.core.SimpleContext;
import co.pishfa.accelerate.service.Service;

import javax.el.ExpressionFactory;
import java.util.Map;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class ExpressionInterpolator {

    private ExpressionFactory factory = ExpressionFactory.newInstance();

    public String populate(final String expression, final Map<String, Object> params) {
        SimpleContext context = new SimpleContext();
        for(Map.Entry<String, Object> param : params.entrySet()) {
            context.setVariable(param.getKey(), factory.createValueExpression(param.getValue(), Object.class));
        }
        return (String) factory.createValueExpression(context, expression, String.class).getValue(context);
    }
}
