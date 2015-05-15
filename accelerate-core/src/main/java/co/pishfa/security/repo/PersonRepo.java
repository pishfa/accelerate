package co.pishfa.security.repo;

import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.security.entity.authentication.Person;

/**
 * 
 * @author Taha Ghasemi
 * 
 */
@Repository
public class PersonRepo extends BaseJpaRepo<Person, Long> {

}
