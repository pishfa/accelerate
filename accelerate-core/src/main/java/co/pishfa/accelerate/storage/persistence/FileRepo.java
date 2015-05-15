package co.pishfa.accelerate.storage.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.accelerate.storage.model.Folder;

/**
 * @author Taha Ghasemi
 * 
 */
@Repository
public class FileRepo extends BaseJpaRepo<File, Long> {

	public static FileRepo getInstance() {
		return CdiUtils.getInstance(FileRepo.class);
	}

    @QueryRunner(where = "e.filename = ?1 and e.folder = ?2")
    public File findByFileNameAndFolder(String filename, Folder folder) {
        return null;
    }
}
