package co.pishfa.accelerate.storage.service;

import co.pishfa.accelerate.storage.model.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * An entry point for management of files in the system. The implementation should pass the request to the appropriate
 * {@link StorageManager} for real file operation and also perform database related tasks (if any).
 * 
 * @author Taha Ghasemi
 */

public interface FileService {
	/**
	 * Uploads the file to the folder place and set the result in out. If out already points to a non-global file, first
	 * deletes it.
	 * 
	 */
	void upload(@NotNull File out, @NotNull UploadedFile file, @NotNull Folder place) throws IOException;
	File upload(@NotNull UploadedFile file, @NotNull Folder place) throws IOException;

	/**
	 * 
	 * @return the web-based address of this file which can be used in a or img tags, although should be sanitized first
	 *         or else XSS is possible
	 */
	String getUrl(@NotNull File file);

	/**
	 * Retrieves the physical file handle related to the passed file.
	 */
	java.io.File download(@NotNull File file);

	boolean isEmpty(@NotNull File file);

	/**
	 * Deletes the file from both storage and database
	 */
	void delete(@NotNull File file) throws IOException;

	void copy(@NotNull File src, @NotNull File dest) throws IOException;

	void add(@NotNull Folder folder) throws IOException;

	/**
	 * Deletes the folder from both storage and database
	 */
	void delete(@NotNull Folder folder) throws IOException;

	/**
	 * @param fullPath in the form storageName://path/filename
	 * @return the file in the given fullPath.
	 */
    File findFile(String fullPath);
    File findFile(String filename, Folder folder);

	/**
	 * @return the folder related to the path in the given storageName. If path is null, returns the default (or
	 *         root) folder of the repository
	 */
	Folder findFolder(String storageName, String path);

	/**
	 * @param fullPath in the form storageName://path or storageName (to get the root folder of storage)
	 */
	Folder findFolder(String fullPath);

    Storage findStorage(String storageName);

	StorageManager getManager(String type);

}
