/**
 * 
 */
package co.pishfa.security.entity.authentication;

import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.hierarchical.HierarchicalEntity;
import co.pishfa.accelerate.persistence.filter.FilterInterval;
import co.pishfa.security.repo.DomainRepo;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * Represents a logical division in the application such as branches, departments, or organizational units.
 * 
 * Domains are hierarchically organized which its code represents where this Domain is placed in this tree. Each
 * node can have at most 4095 direct children and the whole tree depth is at most 6. If the root code starts with say
 * 100, its first child has code 110, second one 120, etc.
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_domain", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@InitEntity(properties = @InitProperty(alias = "parent", name = "parentAndProperties", value = "@parent?"))
public class Domain extends Principal implements HierarchicalEntity<Domain,Long>, FilterInterval {

	private static final long serialVersionUID = 1L;

	private String title;

	@Min(value = 0)
	@Max(value = 5)
	private int depth = 0;

	@Min(value = 1)
	@Max(value = 4095)
	private int nodeOrder = 1; // order among children, 1-based

	private long code = 0x1000000000000000L;
	private long scopeStart = 0x1000000000000000L; // same value as code, renaming is for convenience
	private long scopeEnd = 0x2000000000000000L - 1;

	public static long offset(int level, long nodeOrder) {
		return nodeOrder << ((5 - level) * 12);
	}

	private boolean leaf = true;

	/**
	 * it may inherit it from parent which in this case is null.
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	private SecurityPolicy securityPolicy;

	@Transient
	private SecurityPolicy inheritedSecurityPolicy;

	public Domain() {
	}

	public Domain(String name) {
		setName(name);
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
		setScopeStart(code);
	}

	public long getScopeStart() {
		return scopeStart;
	}

	public void setScopeStart(long scopeStart) {
		this.scopeStart = scopeStart;
	}

	public long getScopeEnd() {
		return scopeEnd;
	}

	public void setScopeEnd(long scopeEnd) {
		this.scopeEnd = scopeEnd;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public boolean isLeaf() {
		return leaf;
	}

	@Override
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	@Override
	public void setParent(Domain p) {
		setDomain(p); // also causes to init. the proxy
	}

	@Override
	public Activation getActivation() {
		return activation;
	}

	@Override
	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	/**
	 * @param secDomain
	 * @return true if this secDomain is a subdomain of the given secDomain
	 */
	public boolean containedIn(Domain secDomain) {
		if (secDomain == null) {
			return false;
		}
		return secDomain.scopeStart <= code && code <= secDomain.scopeEnd;
	}

	/**
	 * Recursively check that this secDomain and all of its predecessors are enabled
	 */
	@Override
	public boolean isActive() {
		return activation.isActive() && (getParent() != null ? getParent().isActive() : true);
	}

	@Override
	public Domain getParent() {
		return getDomain();
	}

	public SecurityPolicy getSecurityPolicy() {
		return securityPolicy;
	}

	public SecurityPolicy getSecurityPolicyInherited() {
		if (inheritedSecurityPolicy == null) {
			inheritedSecurityPolicy = getInheritedSecurityPolicyRec();
		}
		return inheritedSecurityPolicy;
	}

	protected SecurityPolicy getInheritedSecurityPolicyRec() {
		if (securityPolicy != null) {
			return securityPolicy;
		} else {
			return getParent() != null ? getParent().getInheritedSecurityPolicyRec() : null;
		}
	}

    public boolean isSecurityPolicyInherited() {
        return securityPolicy == null;
    }

	public void setSecurityPolicy(SecurityPolicy securityPolicy) {
		this.securityPolicy = securityPolicy;
	}

	public int getNodeOrder() {
		return nodeOrder;
	}

	public void setNodeOrder(int nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	@Override
	public int getPrecedenceBase() {
		return 0 + depth;
	}

	@Override
	public PrincipalType getType() {
		return PrincipalType.DOMAIN;
	}

	@Override
	public List<Domain> getChildren() {
		return DomainRepo.getInstance().findDirectChildren(this, null);
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Object getIntervalStart() {
		return getScopeStart();
	}

	@Override
	public Object getIntervalEnd() {
		return getScopeEnd();
	}

    /**
     * This method is for initialization process only
     */
    public void setParentAndProperties(Domain parent) {
        DomainRepo.getInstance().setParent(parent, this, false);
    }

}