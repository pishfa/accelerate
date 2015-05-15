/**
 * 
 */
package co.pishfa.accelerate.storage.persistence;

import co.pishfa.accelerate.persistence.filter.SimpleFilter;
import co.pishfa.accelerate.persistence.query.QueryBuilder;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.accelerate.storage.model.Folder;

/**
 * @author Taha Ghasemi
 * 
 */
public class FileMgmtFilter extends SimpleFilter<File> {

	private Folder folder;

	public FileMgmtFilter(String viewAction) {
		super(viewAction);
	}

	@Override
	public void addConditions(QueryBuilder<File> query) {
		super.addConditions(query);
		if (folder != null) {
			query.append(" and folder.id = :folderId ").with("folderId", folder.getId());
		}
	}

	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

}
