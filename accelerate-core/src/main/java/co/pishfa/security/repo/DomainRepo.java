package co.pishfa.security.repo;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.filter.Filter;
import co.pishfa.accelerate.persistence.hierarchical.BaseHierarchicalEntityJpaRepo;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.SecurityPolicy;

import java.util.List;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class DomainRepo extends BaseHierarchicalEntityJpaRepo<Domain, Long> {

	private static final String MAIN_DOMAIN_NAME = "main";
	private static final String ROOT_DOMAIN_NAME = "root";
	private static final String REGISTERED_DOMAIN_NAME = "registered";
	private static final String SHARED_DOMAIN_NAME = "shared";

	public static DomainRepo getInstance() {
		return CdiUtils.getInstance(DomainRepo.class);
	}

	private Long sharedDomainId, registeredDomainId, mainDomainId, rootDomainId;

	@Override
	protected String parentPropertyName() {
		return "domain";
	}

	public Domain getSharedDomain() {
		return findById(getSharedDomainId());
	}

	public Long getSharedDomainId() {
		if (sharedDomainId == null) {
			sharedDomainId = findByName(SHARED_DOMAIN_NAME).getId();
		}
		return sharedDomainId;
	}

	public Domain getRegisteredDomain() {
		if (registeredDomainId == null) {
			registeredDomainId = findByName(REGISTERED_DOMAIN_NAME).getId();
		}
		return findById(registeredDomainId);
	}

	public Domain getMainDomain() {
		if (mainDomainId == null) {
			mainDomainId = findByName(MAIN_DOMAIN_NAME).getId();
		}
		return findById(mainDomainId);
	}

    public Domain getRootDomain() {
        if (rootDomainId == null) {
            rootDomainId = findByName(ROOT_DOMAIN_NAME).getId();
        }
        return findById(rootDomainId);
    }

	@SuppressWarnings("unchecked")
	public List<Integer> findChildrenOrders(Domain entity) {
		return getEntityManager()
				.createQuery(
						"select sd.nodeOrder from Domain sd where sd.domain.id = ?1 order by sd.nodeOrder asc")
				.setParameter(1, entity.getId()).getResultList();
	}

	public List<Domain> findByNameSimilarTo(String name, int maxResults, Filter filter) {
		return query().append("select e from Domain e ").where(filter).append(" and name like :name ")
				.with("name", '%' + name + '%').list();
	}

	protected long computeCode(long base, Domain d, int order) {
		return base + Domain.offset(d.getDepth(), order);
	}

	protected void computeCodes(Domain d, Domain parent) {
		d.setCode(computeCode(parent.getCode(), d, d.getNodeOrder()));
		d.setScopeEnd(computeCode(parent.getCode(), d, d.getNodeOrder() + 1) - 1);
	}

	public void assignParent(Domain e, Domain p) {
		if (p != null) {
			e.setDepth(p.getDepth() + 1);
			if (p.isLeaf() == true) {
				p.setLeaf(false);
			}
			// TODO not suitable for distributed case
			synchronized (this.getClass()) {
				// find the first available nodeOrder among the children of the parent
				int nodeOrder = 1;
				for (Integer siblingOrder : DomainRepo.getInstance().findChildrenOrders(p)) {
					if (siblingOrder != nodeOrder) {
						break;
					} else {
						nodeOrder++;
					}
				}
				e.setNodeOrder(nodeOrder);
				computeCodes(e, p);
			}
		}
	}

    @Override
    public void setParent(Domain parent, Domain child, boolean cascadeToChildren) {
        if (parent != null && !parent.equals(child.getParent())) {
            // TODO we should also handle the case of changing parent
            assignParent(child, parent);
        }
        super.setParent(parent, child, cascadeToChildren);
    }

    @QueryRunner("select count(e) from Domain e where e.securityPolicy = ?1")
    public long countByPolicy(SecurityPolicy policy) {
        return 0;
    }
}
