/**
 * 
 */
package co.pishfa.accelerate.storage.model;

import co.pishfa.accelerate.initializer.api.Initializer;
import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitKey;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.hierarchical.BaseHierarchicalEntity;

import javax.persistence.*;
import java.util.List;

/**
 * It may be remote or local
 * 
 * @author Taha Ghasemi
 * 
 */
@Cacheable
@Entity
@Table(name = "ac_folder")
@InitEntity(properties = { @InitProperty(name = "storage", value = "@parent"),
		@InitProperty(name = Initializer.ATTR_ANCHOR, value = "#{this.storage.name}:://#{this.path}") })
public class Folder extends BaseHierarchicalEntity<Folder> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = false)
	@InitKey
	private Storage storage;

	@InitKey
	private String path;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "folder")
	private List<File> files;

	public Folder() {
	}

	public Folder(String name, Folder parent) {
		setName(name);
		setParent(parent);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage location) {
		this.storage = location;
	}

	@Override
	public void setParent(Folder parent) {
		super.setParent(parent);
		if (parent != null) {
			this.path = parent.getPath() + getName() + "/";
			this.storage = parent.getStorage();
		}
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

}
