package co.pishfa.accelerate.storage.persistence;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.persistence.hierarchical.BaseHierarchicalEntityJpaRepo;
import co.pishfa.accelerate.persistence.query.QueryRunner;
import co.pishfa.accelerate.persistence.repository.Repository;
import co.pishfa.accelerate.storage.model.Folder;

/**
 * @author Taha Ghasemi
 * 
 */
@Repository
public class FolderRepo extends BaseHierarchicalEntityJpaRepo<Folder, Long> {

	public static FolderRepo getInstance() {
		return CdiUtils.getInstance(FolderRepo.class);
	}

	@QueryRunner(where = "e.storage.name = ?1 and e.path = ?2")
	public Folder findByStorageNameAndPath(String storageName, String path) {
		return null;
	}

}
