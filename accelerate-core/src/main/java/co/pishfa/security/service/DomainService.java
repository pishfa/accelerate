package co.pishfa.security.service;

import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.service.Service;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.repo.DomainRepo;

import javax.inject.Inject;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class DomainService extends BaseEntityService<Domain, Long> {

    @Inject
    private DomainRepo domainRepo;

    @Override
    public EntityRepository<Domain, Long> getRepository() {
        return domainRepo;
    }
}
