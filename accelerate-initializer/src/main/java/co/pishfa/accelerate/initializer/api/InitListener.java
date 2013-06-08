package co.pishfa.accelerate.initializer.api;

import co.pishfa.accelerate.initializer.model.InitEntityMetaData;

/**
 * A Receiver for {@link Initializer} events.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface InitListener {
	/**
	 * Called when a new entity is created. In this stage, inner children of this entity are not processed but
	 * attributes of this entity is set.
	 * 
	 */
	public void entityCreated(InitEntityMetaData initEntity, Object entityObj);

	/**
	 * Called when the processing of entity and its children are completed.
	 */
	public void entityFinished(InitEntityMetaData initEntity, Object entityObj);

	/**
	 * Only used in incremental mode or in the load section, to find existing entities with the given properties and
	 * values.
	 * 
	 */
	public Object findEntity(InitEntityMetaData initEntity, String[] properties, Object[] values);
}