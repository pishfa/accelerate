/**
 * 
 */
package co.pishfa.security.entity.authorization;

import co.pishfa.accelerate.initializer.api.Initializer;
import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.security.entity.authentication.Principal;
import co.pishfa.security.entity.authentication.PrincipalType;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_representative")
@InitEntity(properties = @InitProperty(name = Initializer.ATTR_IN_PARENT, value = "representative"))
public class Representative extends Principal {

	private static final long serialVersionUID = 1L;

    @Transient
    private AccessRuleDef rule;

	@Override
	public PrincipalType getType() {
		return PrincipalType.CUSTOM_PERM_DEF;
	}

    public void setRule(AccessRuleDef rule) {
        this.rule = rule;
    }

    public AccessRuleDef getRule() {
        return rule;
    }

    public String getTitle() {
        return getRule().getTitle();
    }

}
