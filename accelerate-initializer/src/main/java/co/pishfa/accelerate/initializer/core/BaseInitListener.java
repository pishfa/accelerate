package co.pishfa.accelerate.initializer.core;

import java.util.Map;

import co.pishfa.accelerate.initializer.api.InitListener;
import co.pishfa.accelerate.initializer.model.InitEntityMetadata;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class BaseInitListener implements InitListener {

	@Override
	public void entityCreated(InitEntityMetadata initEntity, Object entityObj) {
	}

	@Override
	public void entityFinished(InitEntityMetadata initEntity, Object entityObj) {
	}

	@Override
	public Object findEntity(InitEntityMetadata initEntity, Map<String, Object> propValues) {
		return null;
	}

}
