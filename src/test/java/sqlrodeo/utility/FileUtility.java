package sqlrodeo.utility;

import java.io.File;

public class FileUtility {

	private FileUtility() {
	}

	public static void recursiveDelete(File dirOrFile) {

		// If it doesn't exist, nothing to do!
		if (!dirOrFile.exists()) {
			return;
		}

		// Delete a file
		if (dirOrFile.isFile()) {
			System.out.println("Deleting: " + dirOrFile);
			dirOrFile.delete();
			return;
		}

		// Delete a directory, starting with the children. (Depth-first)
		File[] children = dirOrFile.listFiles();
		for (File file : children) {
			recursiveDelete(file);
		}
		System.out.println("Deleting: " + dirOrFile);
		dirOrFile.delete();

		if (dirOrFile.exists()) {
			throw new RuntimeException("Could not delete " + dirOrFile);
		}
	}

}
