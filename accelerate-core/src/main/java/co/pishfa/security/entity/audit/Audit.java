package co.pishfa.security.entity.audit;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import co.pishfa.accelerate.i18n.model.ExtendedLocaleTime;
import org.hibernate.validator.constraints.Length;

import co.pishfa.accelerate.i18n.model.ExtendedLocaleDate;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;
import co.pishfa.security.entity.authorization.Action;

/**
 * Audit security operations
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_audit")
public class Audit extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	@Length(max = 1000)
	private String targetTitle;

	private String targetId;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE }, optional = false)
	private Action action;

	@Length(max = 12000)
	private String message;

	@Length(max = 1000)
	private String path;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date creationTime;

	@Embedded
	protected ExtendedLocaleTime finishTime;

	@Enumerated
	@Column(name = "levelColumn")
	private AuditLevel level;

	@Length(max = 250)
	private String host;

	public String getTargetTitle() {
		return targetTitle;
	}

	public void setTargetTitle(String target) {
		this.targetTitle = target;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AuditLevel getLevel() {
		return level;
	}

	public void setLevel(AuditLevel level) {
		this.level = level;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Audit() {
		// TODO Auto-generated constructor stub
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public ExtendedLocaleTime getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(ExtendedLocaleTime finishTime) {
		this.finishTime = finishTime;
	}

}
