package co.pishfa.accelerate.ui.tag;

import java.io.IOException;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.apache.myfaces.view.facelets.el.VariableMapperWrapper;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class ScopeTagHandler extends TagHandler {

	public ScopeTagHandler(TagConfig config) {
		super(config);
	}

	@Override
	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
		VariableMapper orig = ctx.getVariableMapper();
		try {
			VariableMapperWrapper vm = new VariableMapperWrapper(orig);
			ctx.setVariableMapper(vm);
			nextHandler.apply(ctx, parent);
		} finally {
			ctx.setVariableMapper(orig);
		}
	}

}
