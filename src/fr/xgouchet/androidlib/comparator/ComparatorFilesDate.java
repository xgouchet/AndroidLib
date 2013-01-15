package fr.xgouchet.androidlib.comparator;

import java.io.File;
import java.util.Comparator;
import java.util.Locale;

/**
 * Compare files by date (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
public class ComparatorFilesDate implements Comparator<File> {
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
		} else {

			// both are files, or both are folders...
			// get modif date
			final long modif1 = file1.lastModified();
			final long modif2 = file2.lastModified();

			// same extension, we sort alphabetically
			if (modif1 == modif2) {
				locale = Locale.getDefault();
				result = file1.getName().toLowerCase(locale)
						.compareTo(file2.getName().toLowerCase(locale));
			} else {
				result = (int) (modif1 - modif2);
			}
		}

		return result;
	}
}
