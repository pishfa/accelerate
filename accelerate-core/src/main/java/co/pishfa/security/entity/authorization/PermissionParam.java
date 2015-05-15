/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.security.service.handler.BasedOnOtherPermission;

import javax.persistence.*;

/**
 * Useful for custom permissions which instances of this class indicates the exact targets
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_permission_param")
public class PermissionParam extends BaseEntity implements BasedOnOtherPermission {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
	private Permission permission;

	private String target;

	private Long targetId;

	/**
	 * The actual target object, which will not be stored
	 */
	@Transient
	private Object targetObj;

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public Object getTargetObj() {
		return targetObj;
	}

	public void setTargetObj(co.pishfa.accelerate.entity.common.Entity<Long> targetObj) {
		this.targetObj = targetObj;
		this.setTargetId(targetObj.getId());
		this.setTarget(targetObj.getClass().getSimpleName());
	}

	@Override
	public Object getOtherEntity() {
		return getPermission();
	}

}
