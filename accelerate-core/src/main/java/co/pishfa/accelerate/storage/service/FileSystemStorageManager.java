/**
 * 
 */
package co.pishfa.accelerate.storage.service;

import co.pishfa.accelerate.storage.FileUtils;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.accelerate.storage.model.Folder;
import co.pishfa.accelerate.storage.model.UploadedFile;
import co.pishfa.accelerate.ui.UiUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Taha Ghasemi
 * 
 */
public class FileSystemStorageManager implements StorageManager {

	@Override
	public java.io.File upload(UploadedFile file, Folder place, File out) throws IOException {
		String fileName = FileUtils.trimFilePath(file.getName());
		java.io.File uniqueFile = FileUtils.uniqueFile(getPhysicalFile(place), fileName);

		if (file.getFile() != null) {
			org.apache.commons.io.FileUtils.moveFile(file.getFile(), uniqueFile);
		} else {
			FileOutputStream fos;
			fos = new FileOutputStream(uniqueFile);
			fos.write(file.getData());
			fos.close();
		}

		out.setFilename(uniqueFile.getName());

		return uniqueFile;
	}

	@Override
	public String getUrl(File file) {
		StringBuilder path = new StringBuilder(UiUtils.getRequest().getContextPath());

		String address = file.getFolder().getStorage().getUrl();
		if ("downloader".equals(address)) {
			return path.append("/download.do?fileId=").append(file.getId()).toString();
		} else {
			return path.append(address).append(file.getFolder().getPath()).append(file.getFilename()).toString();
		}
	}

	/*
	 * @Override
	 * public File copy(File file) throws IOException {
	 * String path = file.getPath();
	 * File res = null;
	 * if (!CommonUtil.isEmpty(path)) {
	 * int lastIndex = path.lastIndexOf(java.io.File.separatorChar);
	 * String fileName, filePath;
	 * if (lastIndex >= 0) {
	 * fileName = path.substring(lastIndex + 1);
	 * filePath = path.substring(0, lastIndex);
	 * } else {
	 * fileName = path;
	 * filePath = null;
	 * }
	 * 
	 * java.io.File uniqueFile = FileUtil.uniqueFile(new java.io.File(uploadDir + filePath), fileName);
	 * FileUtils.copyFile(new java.io.File(getPhysicalPath(file)), uniqueFile);
	 * 
	 * res = new File();
	 * if (file.getName() != null) {
	 * res.setName(file.getName().clone());
	 * }
	 * res.setPath(filePath + java.io.File.separatorChar + uniqueFile.getName());
	 * res.setLocal(file.isLocal());
	 * }
	 * return res;
	 * }
	 */

	public String getPhysicalPath(File file) {
		return file.getFullPath();
	}

	public String getPhysicalPath(Folder folder) {
		return new StringBuilder(folder.getStorage().getAddress()).append(folder.getPath()).toString();
	}

	public java.io.File getPhysicalFile(Folder folder) {
		return new java.io.File(getPhysicalPath(folder));
	}

	@Override
	public java.io.File getPhysicalFile(File file) {
		return new java.io.File(getPhysicalPath(file));
	}

	@Override
	public void delete(File file) throws IOException {
		org.apache.commons.io.FileUtils.deleteQuietly(getPhysicalFile(file));
	}

	@Override
	public void add(Folder folder) throws IOException {
		org.apache.commons.io.FileUtils.forceMkdir(getPhysicalFile(folder));
	}

	@Override
	public void delete(Folder folder) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(getPhysicalFile(folder));
	}

}
