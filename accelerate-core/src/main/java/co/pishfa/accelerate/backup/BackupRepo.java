package co.pishfa.accelerate.backup;

import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;

import java.util.Date;

/**
 * The repository for {@link co.pishfa.accelerate.backup.Backup} entities.
 *
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Repository
public class BackupRepo extends BaseJpaRepo<Backup, Long> {

    @QueryRunner("delete from Backup where creationDate < ?1")
    public void deleteOlderThan(Date time) {
    }
}
