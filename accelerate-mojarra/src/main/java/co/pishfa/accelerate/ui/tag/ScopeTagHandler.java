package co.pishfa.accelerate.ui.tag;

import com.sun.faces.facelets.el.VariableMapperWrapper;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import java.io.IOException;

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
