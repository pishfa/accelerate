package co.pishfa.accelerate.storage.model;

import java.io.IOException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Transient;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.clone.CloneIgnore;
import co.pishfa.accelerate.clone.CloneShallow;
import co.pishfa.accelerate.content.entity.BaseContentEntity;
import co.pishfa.accelerate.content.entity.ContentEntity;
import co.pishfa.accelerate.i18n.model.MultiStringLong;
import co.pishfa.accelerate.i18n.model.MultiStringLongProxy;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.storage.service.FileService;
import co.pishfa.accelerate.utility.StrUtils;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_file")
public class File extends BaseContentEntity {

	private static final long serialVersionUID = 1L;

	private String contentType;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, optional = false)
	@CloneShallow
    @InitProperty("@parent?")
	private Folder folder;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private MultiStringLong description;

	@Transient
	@CloneIgnore
	private final MultiStringLongProxy descriptionProxy = new MultiStringLongProxy("description", this);

	/**
	 * Whether the file can live without its referencing item
	 */
	private boolean global = false;

    private String filename;

	public MultiStringLong getDescription() {
		return description;
	}

	public void setDescription(MultiStringLong description) {
		this.description = description;
	}

	@PreRemove
	public void preRemove() throws IOException {
		if (!global) {
			CdiUtils.getInstance(FileService.class).delete(this);
		}
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return StrUtils.isEmpty(getName());
	}

	/**
	 * @return the descriptionProxy
	 */
	public MultiStringLongProxy getDescriptionProxy() {
		return descriptionProxy;
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	@Override
	public ContentEntity getOriginalEntity() {
		return null;
	}

	@Override
	public void setOriginalEntity(ContentEntity originalEntity) {
	}

    public String getFullPath() {
        return new StringBuilder(getFolder().getStorage().getAddress()).append(getFolder().getPath()).append(getFilename())
                .toString();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
