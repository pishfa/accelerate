package co.pishfa.security.service;

import co.pishfa.accelerate.persistence.repository.EntityRepository;
import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.BaseEntityService;
import co.pishfa.accelerate.service.Service;
import co.pishfa.security.entity.authentication.Domain;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.repo.UserRepo;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class UserService extends BaseEntityService<User, Long>{

    @Inject
    private UserRepo userRepo;

    @Override
    public EntityRepository<User, Long> getRepository() {
        return userRepo;
    }

    @Action("password.change")
    public void changePassword(User user) {
        userRepo.edit(user);
    }

    public List<User> findByDomain(@NotNull Domain domain) {
        return userRepo.findByDomain(domain);
    }
}
