/**
 * 
 */
package co.pishfa.accelerate.storage.model;

import java.util.List;

import javax.persistence.*;

import co.pishfa.security.entity.authorization.BaseSecuredEntity;

/**
 * An abstraction of the root location (local or remote) where files and folders can be stored. The internal handling of storage is
 * dependent on the type of repository.
 * 
 * @author Taha Ghasemi
 * 
 */
@Cacheable
@Entity
@Table(name = "ac_storage")
public class Storage extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	private String address;

	@Column(name = "url_col")
	private String url;

	@Column(name = "type_col")
	private String type;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "storage")
	private List<Folder> folders;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String webAddress) {
		this.url = webAddress;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

    /**
     * Used for initialization
     */
    public void addChild(Folder folder) {
    }

}
