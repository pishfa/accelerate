/**
 * 
 */
package co.pishfa.accelerate.content.entity;

import co.pishfa.security.entity.authorization.BaseSecuredEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
public abstract class BaseContentEntity extends BaseSecuredEntity implements ContentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.ORDINAL)
	private LockType lockType = LockType.FREE;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime = new Date();

	@Enumerated(EnumType.ORDINAL)
	private LifecycleState lifecycleState = LifecycleState.DRAFT;

	private String title;

	@Override
	public LockType getLockType() {
		return lockType;
	}

	public void setLockType(LockType lockType) {
		this.lockType = lockType;
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	@Override
	public void setLifecycleState(LifecycleState lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public void setCreationTime(Date date) {
		this.creationTime = date;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
