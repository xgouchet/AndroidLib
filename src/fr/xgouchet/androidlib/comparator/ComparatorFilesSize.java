package fr.xgouchet.androidlib.comparator;

import android.annotation.SuppressLint;
import java.io.File;
import java.util.Comparator;

/**
 * Compare files by size (w/ folders listed first)
 * 
 * @author x.gouchet
 * 
 */
@SuppressLint("DefaultLocale")
public class ComparatorFilesSize implements Comparator<File> {
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(final File file1, final File file2) {
		int result;

		// sort folders first
		if ((file1.isDirectory()) && (!file2.isDirectory())) {
			result = -1;
		} else if ((!file1.isDirectory()) && (file2.isDirectory())) {
			result = 1;
		} else if ((file1.isDirectory()) && (file2.isDirectory())) {
			// if both are folders
			result = file1.getName().toLowerCase()
					.compareTo(file2.getName().toLowerCase());
		} else {

			// both are files, we get the sizes
			final long size1 = file1.length();
			final long size2 = file2.length();

			// same extension, we sort alphabetically
			if (size1 == size2) {
				result = file1.getName().toLowerCase()
						.compareTo(file2.getName().toLowerCase());
			} else {
				result = (int) (size1 - size2);
			}
		}

		return result;
	}
}
