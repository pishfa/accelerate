/**
 * 
 */
package co.pishfa.accelerate.entity.common;

import co.pishfa.accelerate.cdi.Veto;
import co.pishfa.accelerate.meta.entity.EntityMetadata;

import java.io.Serializable;

/**
 * Represents an entity in the modeling. Entities are uniquely identified by a key.
 * 
 * @author Taha Ghasemi
 *
 * @param <K> Type of key
 */
@Veto
public interface Entity<K> extends Serializable {

	K getId();

	void setId(K id);

	/**
	 * TODO: must be removed
	 * Name is usually a unique (within some scope) human-readable name that can be used to reference this object by
	 * human. It is more or less is like a natural key for the object, if available. It might be null.
	 */
	String getName();

	/**
	 * TODO: must be removed
	 * @return the user-friendly name of this entity. It might be null.
	 */
	String getTitle();

	/**
	 * TODO: must be removed
	 * @return
	 */
	EntityMetadata<? extends Entity<K>,K> getMetadata();

}