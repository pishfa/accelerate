package co.pishfa.accelerate.config.entity;

import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.common.BaseEntity;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: PersistentConfigEntry
 * 
 * @author Taha Ghasemi
 */
@Entity
@Table(name = "ac_persistent_config_entry")
public class PersistentConfigEntry extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public PersistentConfigEntry() {
	}

    public PersistentConfigEntry(String name, PersistentConfig config, Object value) {
        this.setName(name);
        this.config = config;
        this.setValue(value == null? null : value.toString());
    }

	private String value;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = false)
	@InitProperty("@parent(1)")
	private PersistentConfig config;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
