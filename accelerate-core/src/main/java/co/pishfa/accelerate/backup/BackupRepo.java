package co.pishfa.accelerate.backup;

import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;

import java.util.Date;
import java.util.List;

/**
 * The repository for {@link co.pishfa.accelerate.backup.Backup} entities.
 *
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Repository
public class BackupRepo extends BaseJpaRepo<Backup, Long> {

    @QueryRunner(where = "e.creationDate < ?1")
    public List<Backup> findOlderThan(Date time) {
        return null;
    }

    @QueryRunner(maxResults = 1, value = "select e.creationDate from Backup e where e.status = ?1 order by e.creationDate desc")
    public Date findLastDate(Backup.BackupStatus status) {
        return null;
    }

    @QueryRunner("delete from Backup where creationDate < ?1")
    public void deleteOlderThan(Date time) {
    }
}
