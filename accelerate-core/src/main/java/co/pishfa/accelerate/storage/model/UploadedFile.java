package co.pishfa.accelerate.storage.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Represents a single file for uploading. This might be memory based or file based.
 * 
 * @author Ghasemi
 * 
 */
public class UploadedFile {
	// either data or file contain the actual data dependent on memory or file based uploading system
	private byte[] data;
	private File file = null;
	private String name;
	private String contentType;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUploaded() {
		return file != null || data != null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream openAsStream() throws FileNotFoundException {
		if (file != null) {
			return new FileInputStream(file);
		} else {
			return new ByteArrayInputStream(data);
		}
	}

	public Reader openAsReader() throws FileNotFoundException, UnsupportedEncodingException {
		return new InputStreamReader(openAsStream(), "UTF-8");
	}

}
