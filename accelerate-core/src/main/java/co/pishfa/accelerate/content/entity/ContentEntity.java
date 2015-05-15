/**
 * 
 */
package co.pishfa.accelerate.content.entity;

import co.pishfa.security.entity.authorization.SecuredEntity;

import java.util.Date;

/**
 * Base for contents with approval workflow, versioning, and history capabilities.
 * ContentEntity holds who and when modifies it.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface ContentEntity<K> extends SecuredEntity<K> {

	public enum LockType {
		FREE, READ_LOCK, WRITE_LOCK;
	}

	public int getVersion();

	public Date getCreationTime();

	public void setCreationTime(Date date);

	public LifecycleState getLifecycleState();

	public void setLifecycleState(LifecycleState state);

	public ContentEntity getOriginalEntity();

	public void setOriginalEntity(ContentEntity originalEntity);

	public LockType getLockType();

}
