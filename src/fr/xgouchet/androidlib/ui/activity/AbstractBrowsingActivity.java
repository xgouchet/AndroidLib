package fr.xgouchet.androidlib.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;
import fr.xgouchet.androidlib.R;
import fr.xgouchet.androidlib.comparator.ComparatorFilesAlpha;
import fr.xgouchet.androidlib.data.FileUtils;
import fr.xgouchet.androidlib.ui.adapter.FileListAdapter;

/**
 * 
 */
public abstract class AbstractBrowsingActivity extends Activity implements
		OnItemClickListener {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(final Bundle savedState) {
		super.onCreate(savedState);

		mExtWhiteList = new ArrayList<String>();
		mExtBlackList = new ArrayList<String>();
		mComparator = new ComparatorFilesAlpha();
		mListAdapter = new FileListAdapter(this, new LinkedList<File>(), null);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();

		// Setup the widget
		mFilesList = (ListView) findViewById(android.R.id.list);
		mFilesList.setOnItemClickListener(this);

		// set adpater
		mFilesList.setAdapter(mListAdapter);

		// initial folder
		File folder;
		if (mCurrentFolder != null) {
			folder = mCurrentFolder;
		} else if ((FileUtils.STORAGE.exists())
				&& (FileUtils.STORAGE.canRead())) {
			folder = FileUtils.STORAGE;
		} else {
			folder = new File("/");
		}

		fillFolderView(folder);
	}

	/**
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	public void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long itemId) {
		File file, canon;

		file = mList.get(position);
		canon = new File(FileUtils.getCanonizePath(file));

		// safe check : file exists
		if (file.exists()) {
			if (file.isDirectory()) {
				if (onFolderClick(file)) {
					fillFolderView(canon);
				}
			} else {
				onFileClick(canon);
			}
		}
	}

	/**
	 * @param folder
	 *            the folder being clicked
	 * @return if the folder should be opened in the browsing list view
	 */
	protected abstract boolean onFolderClick(File folder);

	/**
	 * @param file
	 *            the file being clicked (it is not a folder)
	 */
	protected abstract void onFileClick(File file);

	/**
	 * Folder view has been filled
	 */
	protected abstract void onFolderViewFilled();

	/**
	 * Fills the files list with the specified folder
	 * 
	 * @param file
	 *            the file of the folder to display
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void fillFolderView(final File folder) {
		final File file = new File(FileUtils.getCanonizePath(folder));

		if (!file.exists()) {
			Crouton.makeText(this, R.string.toast_folder_doesnt_exist,
					Style.ALERT).show();
		} else if (!file.isDirectory()) {
			Crouton.makeText(this, R.string.toast_folder_not_folder,
					Style.ALERT).show();
		} else if (!file.canRead()) {
			Crouton.makeText(this, R.string.toast_folder_cant_read, Style.ALERT)
					.show();
		} else {

			listFiles(file);

			// create string list adapter
			// mListAdapter = new FileListAdapter(this, mList, file);
			mListAdapter.clear();
			mListAdapter.setCurrentFolder(file);
			if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
				mListAdapter.addAll(mList);
			} else {
				for (File f : mList) {
					mListAdapter.add(f);
				}
			}
			mFilesList.scrollTo(0, 0);

			// update path
			mCurrentFolder = file;
			setTitle(file.getName());

			onFolderViewFilled();
		}
	}

	/**
	 * List the files in the given folder and store them in the list of files to
	 * display
	 * 
	 * @param folder
	 *            the folder to analyze
	 */
	protected void listFiles(final File folder) {
		File file;

		// get files list as array list
		if ((folder == null) || (!folder.isDirectory())) {
			mList = new ArrayList<File>();
			return;
		}

		mList = new ArrayList<File>(Arrays.asList(folder.listFiles()));

		// filter files
		for (int i = (mList.size() - 1); i >= 0; i--) {
			file = mList.get(i);

			// remove
			if (!(isFileVisible(file) && isFileTypeAllowed(file))) {
				mList.remove(i);
			}
		}

		// Sort list
		if (mComparator != null) {
			Collections.sort(mList, mComparator);
		}

		// Add parent folder
		if (!folder.getPath().equals("/")) {
			mList.add(0, folder.getParentFile());
		}
	}

	protected boolean isFileVisible(final File file) {

		boolean visible = true;

		// filter hidden files
		if ((!mShowHiddenFiles) && (file.getName().startsWith("."))) {
			visible = false;
		}

		// filter non folders
		if (mShowFoldersOnly && (!file.isDirectory())) {
			visible = false;
		}

		return visible;
	}

	/**
	 * Filters files based on their extensions and white list / black list
	 * 
	 * @param file
	 *            the file to test
	 * @return if the file can be shown (either appear in white list or doesn't
	 *         appear on blacklist)
	 */
	protected boolean isFileTypeAllowed(final File file) {
		boolean allow = true;
		String ext;

		if (file.isFile()) {
			ext = FileUtils.getFileExtension(file);
			if ((mExtWhiteList != null) && (!mExtWhiteList.isEmpty())
					&& (!mExtWhiteList.contains(ext))) {
				allow = false;
			}

			if ((mExtBlackList != null) && (!mExtBlackList.isEmpty())
					&& (mExtBlackList.contains(ext))) {
				allow = false;
			}
		}

		return allow;
	}

	/** The list of files to display */
	protected List<File> mList;
	/** the dialog's list view */
	protected ListView mFilesList;
	/** The list adapter */
	protected FileListAdapter mListAdapter;

	/** the current folder */
	protected File mCurrentFolder;

	/** the current file sort */
	protected Comparator<File> mComparator;

	protected boolean mShowFoldersOnly = false;
	protected boolean mShowHiddenFiles = true;
	protected boolean mHideLockedFiles = false;
	protected List<String> mExtWhiteList;
	protected List<String> mExtBlackList;
}
