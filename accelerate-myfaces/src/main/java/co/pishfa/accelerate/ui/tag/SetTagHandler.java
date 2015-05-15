package co.pishfa.accelerate.ui.tag;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class SetTagHandler extends TagHandler {

	private final TagAttribute var;
	private final TagAttribute value;

	public SetTagHandler(TagConfig config) {
		super(config);
		var = getRequiredAttribute("var");
		value = getRequiredAttribute("value");
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
		String variable = this.var.getValue(ctx);
		ValueExpression obj = this.value.getValueExpression(ctx, Object.class);
		/*Object resolved = obj.getValue(ctx);
		ctx.getVariableMapper().setVariable(variable,
				ctx.getExpressionFactory().createValueExpression(resolved, Object.class));*/
		ctx.getVariableMapper().setVariable(variable, obj);
	}

}
