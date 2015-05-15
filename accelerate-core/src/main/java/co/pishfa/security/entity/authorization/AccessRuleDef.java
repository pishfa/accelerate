/**
 * 
 */
package co.pishfa.security.entity.authorization;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
public abstract class AccessRuleDef extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	protected Representative representative;

	public Representative getRepresentative() {
		return representative;
	}

	public void setRepresentative(Representative representative) {
		this.representative = representative;
	}

}
