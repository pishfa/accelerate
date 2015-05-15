/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.security.entity.authorization.OwnableEntity;

import javax.persistence.*;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_profile")
public class Profile extends BaseEntity implements OwnableEntity {

	private static final long serialVersionUID = 1L;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, mappedBy = "profile")
	private User user;

	public Profile() {
	}

	public Profile(User user) {
		super();
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public User getOwner() {
		return getUser();
	}

}
