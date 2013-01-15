package fr.xgouchet.androidlib.comparator;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;

import fr.xgouchet.androidlib.data.FileUtils;

/**
 * Compare files by type (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
public class ComparatorFilesType implements Comparator<File> {
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(final File file1, final File file2) {
		int result;
		Locale locale;

		// sort folders first
		if ((file1.isDirectory()) && (!file2.isDirectory())) {
			result = -1;
		} else if ((!file1.isDirectory()) && (file2.isDirectory())) {
			result = 1;
		} else if ((file1.isDirectory()) && (file2.isDirectory())) {
			// if both are folders
			locale = Locale.getDefault();
			result = file1.getName().toLowerCase(locale)
					.compareTo(file2.getName().toLowerCase(locale));
		} else {

			// both are files, we get the extension
			final String ext1 = FileUtils.getFileExtension(file1);
			final String ext2 = FileUtils.getFileExtension(file2);

			// same extension, we sort alphabetically
			locale = Locale.getDefault();
			if (ext1.equalsIgnoreCase(ext2)) {
				result = file1.getName().toLowerCase(locale)
						.compareTo(file2.getName().toLowerCase(locale));
			} else {
				result = ext1.toLowerCase(locale).compareTo(
						ext2.toLowerCase(locale));
			}
		}

		return result;
	}
}
