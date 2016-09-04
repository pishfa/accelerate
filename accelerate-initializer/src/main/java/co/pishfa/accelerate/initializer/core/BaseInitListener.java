package co.pishfa.accelerate.initializer.core;

import java.util.Map;

import co.pishfa.accelerate.initializer.api.InitListener;
import co.pishfa.accelerate.initializer.model.InitEntityMetaData;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class BaseInitListener implements InitListener {

	@Override
	public void entityCreated(InitEntityMetaData initEntity, Object entityObj) {
	}

	@Override
	public void entityFinished(InitEntityMetaData initEntity, Object entityObj) {
	}

	@Override
	public Object findEntity(InitEntityMetaData initEntity, Map<String, Object> propValues) {
		return null;
	}

}
