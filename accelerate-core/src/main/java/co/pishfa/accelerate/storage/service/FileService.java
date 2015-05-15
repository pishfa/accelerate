package co.pishfa.accelerate.storage.service;

import co.pishfa.accelerate.storage.model.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * An entry point for management of files in the system. The implementation should pass the request to the appropriate
 * {@link StorageManager}.
 * 
 * @author Taha Ghasemi
 */

public interface FileService {
	/**
	 * Uploads the file to the folder place and set the result in out. If out already points to a non-global file, first
	 * deletes it.
	 * 
	 */
	public void upload(@NotNull File out, @NotNull UploadedFile file, @NotNull Folder place) throws IOException;

	public void upload(@NotNull MultiUploadedFile file, @NotNull Folder place, @NotNull MultiFile out) throws IOException;

	/**
	 * 
	 * @return the web-based address of this file which can be used in a or img tags, although should be sanitized first
	 *         or else XSS is possible
	 */
	public String getUrl(@NotNull File file);

	/**
	 * Retrieves the physical file handle related to the passed file.
	 */
	public java.io.File download(@NotNull File file);

	public boolean isEmpty(@NotNull File file);

	/**
	 * Deletes the file from both storage and database
	 */
	public void delete(@NotNull File file) throws IOException;

	public void copy(@NotNull File src, @NotNull File dest) throws IOException;

	public void add(@NotNull Folder folder) throws IOException;

	/**
	 * Deletes the folder from both storage and database
	 */
	public void delete(@NotNull Folder folder) throws IOException;

    public File findFile(String name);
    public File findFile(String filename, Folder folder);

	/**
	 * @return the folder related to the path in the given storageName. If path is null, returns the default (or
	 *         root) folder of the repository
	 */
	public Folder findFolder(String storageName, String path);

    public Storage findStorage(String storageName);

}
