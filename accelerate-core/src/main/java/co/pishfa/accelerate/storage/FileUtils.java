package co.pishfa.accelerate.storage;

import java.io.File;
import java.io.IOException;

/**
 * Common file operation functions
 * 
 */
public final class FileUtils {

	/**
	 * Trim the eventual file path from the given file name. Anything before the last occurred "/" and "\" will be trimmed, including the
	 * slash.
	 * 
	 * @param fileName
	 *            The file name to trim the file path from.
	 * @return The file name with the file path trimmed.
	 */
	public static String trimFilePath(String fileName) {
		return fileName.substring(fileName.lastIndexOf("/") + 1).substring(fileName.lastIndexOf("\\") + 1);
	}

	/**
	 * Generate unique file based on the given path and name. If the file exists, then it will add "[i]" to the file name as long as the
	 * file exists. The value of i can be between 0 and 2147483647 (the value of Integer.MAX_VALUE).
	 * 
	 * @param filePath
	 *            The path of the unique file.
	 * @param fileName
	 *            The name of the unique file.
	 * @return The unique file.
	 * @throws IOException
	 *             If unique file cannot be generated, this can be caused if all file names are already in use. You may consider another
	 *             filename instead.
	 */
	public static File uniqueFile(File filePath, String fileName) throws IOException {
		File file = new File(filePath, fileName);

		if (file.exists()) {

			// Split filename and add braces, e.g. "name.ext" --> "name[",
			// "].ext".
			String prefix;
			String suffix;
			int dotIndex = fileName.lastIndexOf(".");

			if (dotIndex > -1) {
				prefix = fileName.substring(0, dotIndex) + "[";
				suffix = "]" + fileName.substring(dotIndex);
			} else {
				prefix = fileName + "[";
				suffix = "]";
			}

			int count = 0;

			// Add counter to filename as long as file exists.
			while (file.exists()) {
				if (count < 0) { // int++ restarts at -2147483648 after
									// 2147483647.
					throw new IOException("No unique filename available for " + fileName + " in path " + filePath.getPath() + ".");
				}

				// Glue counter between prefix and suffix, e.g. "name[" + count
				// + "].ext".
				file = new File(filePath, prefix + (count++) + suffix);
			}
		}

		return file;
	}

}
