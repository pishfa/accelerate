/**
 * 
 */
package co.pishfa.security.entity.authorization;

import javax.persistence.*;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.common.BaseEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_permission_def_param")
@Cacheable
@InitEntity(key = "-")
public class PermissionDefParam extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = false)
	@InitProperty("@parent(1)")
	private PermissionDef definition;

	private String value;

	public PermissionDef geDefinition() {
		return definition;
	}

	public void setDefinition(PermissionDef definition) {
		this.definition = definition;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
