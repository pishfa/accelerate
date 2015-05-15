/**
 * 
 */
package co.pishfa.accelerate.config.entity;

import co.pishfa.accelerate.content.entity.BaseContentEntity;
import co.pishfa.accelerate.content.entity.ContentEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains configurations that are stored in the database and can be changed by the user.
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_persistent_config")
public class PersistentConfig extends BaseContentEntity {

	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "config")
	private List<PersistentConfigEntry> entries = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = true)
	private PersistentConfig original;

	@Override
	public PersistentConfig getOriginalEntity() {
		return original;
	}

	@Override
	public void setOriginalEntity(ContentEntity originalEntity) {
		this.original = (PersistentConfig) originalEntity;
	}

	public List<PersistentConfigEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<PersistentConfigEntry> entries) {
		this.entries = entries;
	}

}
