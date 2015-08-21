/**
 * 
 */
package co.pishfa.accelerate.storage.service;

import co.pishfa.accelerate.storage.model.File;
import co.pishfa.accelerate.storage.model.Folder;
import co.pishfa.accelerate.storage.model.UploadedFile;

import java.io.IOException;

/**
 * @author Taha Ghasemi
 * 
 */
public interface StorageManager {

	String getName();

	void upload(UploadedFile file, Folder place, File out) throws IOException;

	String getUrl(File file);

	java.io.File download(File file);

	void delete(File file) throws IOException;

	void add(Folder folder) throws IOException;

	void delete(Folder folder) throws IOException;

}
