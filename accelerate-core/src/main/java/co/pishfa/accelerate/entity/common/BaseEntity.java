package co.pishfa.accelerate.entity.common;

import co.pishfa.accelerate.cdi.Veto;
import co.pishfa.accelerate.clone.CloneIgnore;
import co.pishfa.accelerate.clone.CloneNull;
import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.security.entity.audit.Audit;
import co.pishfa.security.entity.audit.Auditable;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

/**
 * The base implementation of {@link Entity}. This class provides long id, hashCode,and equals method.
 * 
 * @author Taha Ghasemi
 * 
 */
@MappedSuperclass
@Veto
@Table(indexes = @Index(columnList = "name"))
public class BaseEntity implements Entity<Long>, Auditable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@CloneNull
	private Long id;

	@Version
	@CloneIgnore
	private int version = 0;

	@Length(max = 256)
	private String name;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getId() == null) {
			return false;
		}

        Class objClass = ProxyUtils.getUnproxiedClass(obj.getClass());
        Class thisClass = ProxyUtils.getUnproxiedClass(this.getClass());
		if (!thisClass.equals(objClass)) {
			return false;
		}

		return getId().equals(((BaseEntity) obj).getId());
	}

	@Override
	public Long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		if (getId() == null) {
			return super.hashCode();
		}
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (getId() ^ (getId() >>> 32));
		return result;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "-" + name + "-" + id;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public EntityMetadata<? extends Entity<Long>,Long> getMetadata() {
		return EntityMetadataService.getInstance().getEntityMetadata(this.getClass(),Long.class);
	}

    @Override
    public void audit(Audit audit) {
        if(getId() != null)
            audit.setTargetId(getId().toString());
        if(getTitle() != null)
            audit.setTargetTitle(getTitle());
    }

}
