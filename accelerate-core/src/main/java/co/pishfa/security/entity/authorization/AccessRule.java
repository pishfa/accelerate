/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.security.entity.authentication.Activation;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authentication.PrincipalType;

import javax.persistence.*;
import javax.validation.constraints.Min;

/**
 * Represents an access control rule for a particular principal
 * 
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
@InitEntity
public abstract class AccessRule extends BaseEntity {

	private static final long serialVersionUID = 1L;

	protected Long principalId;

	@Enumerated(EnumType.ORDINAL)
	protected PrincipalType principalType;

	@Transient
	@InitProperty("@parent(1)")
	protected Principal principal;

	@Min(value = 0)
	protected int precedence = 0;

	@Embedded
	protected Activation activation = new Activation();

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "TypeColumn")
	protected AccessRuleType type = AccessRuleType.ALLOW;

	public Activation getActivation() {
		return activation;
	}

	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	public AccessRuleType getType() {
		return type;
	}

	public void setType(AccessRuleType accessType) {
		this.type = accessType;
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	public Principal getPrincipal() {
		if (principal == null && principalId != null) {
			principal = principalType.getRepository().findById(principalId);
		}
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
		if (principal != null) {
			principalId = principal.getId();
			principalType = principal.getType();
			setPrecedence(principal.getPrecedenceBase());
		} else {
			principalId = null;
			principalType = null;
		}
	}

	public boolean isActive() {
		return activation.isActive();
	}

}
