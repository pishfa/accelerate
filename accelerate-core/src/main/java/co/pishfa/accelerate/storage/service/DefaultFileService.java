package co.pishfa.accelerate.storage.service;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.service.Action;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.storage.model.*;
import co.pishfa.accelerate.storage.persistence.FileRepo;
import co.pishfa.accelerate.storage.persistence.FolderRepo;
import co.pishfa.accelerate.storage.persistence.StorageRepo;
import co.pishfa.accelerate.ui.UiUtils;
import co.pishfa.accelerate.validation.AutoValidating;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Default file manager service implementation.
 * 
 * @author Taha Ghasemi
 */
@Service
@Named("fileService")
public class DefaultFileService implements FileService {

	@Inject
	@Any
	Instance<StorageManager> storageManagers;

	private final Map<String, StorageManager> managers = new HashMap<>();

    @Inject
    private FileRepo fileRepo;

	@Inject
	private FolderRepo folderRepo;

    @Inject
    private StorageRepo storageRepo;

	public static FileService getInstance() {
		return CdiUtils.getInstance(DefaultFileService.class);
	}

	@PostConstruct
	public void init() {
		for(StorageManager storageManager : storageManagers) {
			managers.put(storageManager.getName(), storageManager);
		}
	}

	@Override
	public StorageManager getManager(String type) {
		return managers.get(type);
	}

	@Override
	public File upload(@NotNull UploadedFile file, @NotNull Folder place) throws IOException {
		File res = new File();
		upload(res, file, place);
		return res;
	}

	@Action
	@Override
	public void upload(File out, UploadedFile file, Folder place) throws IOException {
		Validate.notEmpty(file.getName(), "File name is empty");
		Validate.isTrue(file.getData() != null || file.getFile() != null, "No data is available");

		// first delete the old one
		if (!isEmpty(out) && !out.isGlobal()) {
			delete(out);
		}

		managerOf(place).upload(file, place, out);
		out.setFolder(place);
		out.setContentType(file.getContentType());
	}

	@Override
	public String getUrl(File file) {
		if (isEmpty(file)) {
			return null;
		}
		if ("downloader".equals(file.getFolder().getStorage().getUrl())) {
			StringBuilder path = new StringBuilder(UiUtils.getRequest().getContextPath());
			return path.append("/download.do?fileId=").append(file.getId()).toString();
		} else
			return managerOf(file).getUrl(file);
	}

	@Override
	public boolean isEmpty(File file) {
		return file == null || file.isEmpty();
	}

	@Action
	@Override
	public java.io.File download(File file) {
		return managerOf(file).download(file);
	}

    @Override
    public File findFile(String fullPath) {
		int fileIndex = fullPath.lastIndexOf("/");
		return findFile(fullPath.substring(fileIndex + 1), findFolder(fullPath.substring(0, fileIndex)));
    }

    @Override
    public File findFile(String name, Folder folder) {
        return fileRepo.findByNameAndFolder(name, folder);
    }

    @Override
	public Folder findFolder(String storageName, String path) {
		return folderRepo.findByStorageNameAndPath(storageName, path == null ? "/" : path);
	}

	@Override
	public Folder findFolder(String fullPath) {
		String[] parts = fullPath.split("://");
		return findFolder(parts[0], parts.length>1?parts[1]:null);
	}

	@Override
    public Storage findStorage(String storageName) {
        return storageRepo.findByName(storageName);
    }

    @Action
	@Override
	public void delete(File file) throws IOException {
		managerOf(file).delete(file);
		fileRepo.delete(file);
	}

	@Action
	@Override
	public void add(Folder folder) throws IOException {
		managerOf(folder).add(folder);
		folderRepo.add(folder);
	}

	@Action
	@Override
	public void delete(Folder folder) throws IOException {
		managerOf(folder).delete(folder);
		folderRepo.delete(folder);
	}

	protected StorageManager managerOf(File file) {
		return managerOf(file.getFolder());
	}

	protected StorageManager managerOf(Folder folder) {
		return getManager(folder.getStorage().getType());
	}

	@AutoValidating
	@Override
	public void copy(File src, File dest) throws IOException {
		java.io.File file = download(src);
		java.io.File tempFile = java.io.File.createTempFile(src.getName(), null);
		FileUtils.copyFile(file, tempFile);
		UploadedFile uFile = new UploadedFile();
		uFile.setName(dest.getName());
		dest.setName(null); // make it as empty, or else upload will delete it
		uFile.setFile(tempFile);
		uFile.setContentType(src.getContentType());
		upload(dest, uFile, dest.getFolder());
	}

}
