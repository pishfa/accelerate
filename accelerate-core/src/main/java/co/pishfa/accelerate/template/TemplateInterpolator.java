/**
 * 
 */
package co.pishfa.accelerate.template;

import co.pishfa.accelerate.resource.ResourceUtils;
import co.pishfa.accelerate.service.Service;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@Service
public class TemplateInterpolator {

	private final VelocityEngine engine;

	public TemplateInterpolator() throws Exception {
		engine = new VelocityEngine();
		Properties properties = new Properties();
		properties.load(ResourceUtils.getResourceAsStream("velocity.properties"));
		engine.init(properties);
	}

	public String populate(final String template, final Map<String, Object> params) throws Exception {
		VelocityContext context = new VelocityContext(params);
		StringWriter writer = new StringWriter();
		engine.mergeTemplate(template, "UTF-8", context, writer);
		return writer.toString();
	}

}
