package fr.xgouchet.androidlib.ui.adapter;

import static fr.xgouchet.androidlib.data.FileUtils.DOWNLOAD_FOLDER;
import static fr.xgouchet.androidlib.data.FileUtils.STORAGE_PATH;
import static fr.xgouchet.androidlib.data.FileUtils.getMimeType;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.xgouchet.androidlib.R;
import fr.xgouchet.androidlib.common.UIUtils;
import fr.xgouchet.androidlib.data.FileUtils;

/**
 * A File List Adapter used to display folders and files
 * 
 */
public class FileListAdapter extends ArrayAdapter<File> {

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context
	 * @param objects
	 *            The objects to represent in the ListView.
	 * @param folder
	 *            the parent folder of the items presented, or null if the top
	 *            folder should not be displayed as up
	 */
	public FileListAdapter(Context context, List<File> objects, File folder) {
		this(context, objects, folder, R.layout.item_file);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            The current context
	 * @param objects
	 *            The objects to represent in the ListView.
	 * @param folder
	 *            the parent folder of the items presented, or null if the top
	 *            folder should not be displayed as up
	 * @param layout
	 *            the layout to use
	 */
	public FileListAdapter(Context context, List<File> objects, File folder,
			int layout) {
		super(context, layout, objects);
		mLayout = layout;
		mFolder = folder;
	}

	/**
	 * @see ArrayAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		File file;
		View v;
		TextView textView;
		String text;
		int icon, style;

		// recycle the view
		v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_file, null);
		}

		// get the file infos
		file = getItem(position);
		style = Typeface.BOLD;
		if (file != null) {
			text = file.getName();

			if ((position == 0) && (mFolder != null)
					&& (file.equals(mFolder.getParentFile()))
					&& (!mFolder.getPath().equals("/"))) {
				icon = R.drawable.up;
				text = "";
			} else {
				if (FileUtils.isSymLink(file)) {
					File target = FileUtils.getSymLinkTarget(file);
					if (target.equals(FileUtils.STORAGE)) {
						icon = R.drawable.sd_card;
					} else if (target.isDirectory()) {
						icon = R.drawable.folder_link;
					} else {
						icon = R.drawable.file_link;
					}
				} else {
					icon = getIconForFile(file);
				}
			}
		} else {
			text = "";
			icon = R.drawable.file_unknown;
		}

		Drawable thumbnail = null;
		Drawable selectedIcon = null;

		// File Icon / Thumbnail
		if (mThumbnailProvider != null) {
			thumbnail = mThumbnailProvider.getThumbnailForFile(getContext(),
					file);
		}
		if (thumbnail == null) {
			thumbnail = getContext().getResources().getDrawable(icon);
		}

		int size = UIUtils.getPxFromDp(getContext(), 38);

		if (thumbnail != null) {
			double ratio = ((double) thumbnail.getIntrinsicWidth())
					/ ((double) thumbnail.getIntrinsicHeight());
			if (ratio > 1) {
				thumbnail.setBounds(0, 0, size, (int) (size / ratio));
			} else {
				thumbnail.setBounds(0, 0, (int) (size * ratio), size);
			}
		}

		// Handle selection
		int color = Color.LTGRAY;
		if ((mSelection != null) && (mSelection.contains(file))) {
			selectedIcon = getContext().getResources().getDrawable(
					R.drawable.selected);
			selectedIcon.setBounds(0, 0, size, size);
			style = Typeface.BOLD_ITALIC;
			color = Color.rgb(0, 192, 0);
		}

		// Setup name and icon
		textView = (TextView) v.findViewById(R.id.textFileName);
		if (textView != null) {
			textView.setText(text);

			if (mIconOnTop) {
				textView.setCompoundDrawables(null, thumbnail, null, null);
				textView.setGravity(Gravity.CENTER);
			} else {
				textView.setCompoundDrawables(thumbnail, null, selectedIcon,
						null);
				textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			}

			textView.setTypeface(Typeface.DEFAULT, style);

			textView.setEllipsize(TruncateAt.MIDDLE);
			textView.setSingleLine();
			textView.setTextColor(color);
		}

		return v;
	}

	public static int getIconForFile(File file) {
		int id;
		boolean locked, priv;
		String type, ext;

		priv = !file.canRead();
		locked = file.canRead() && !file.canWrite();

		if (file.getPath().equals(STORAGE_PATH)) { // External storage
			id = R.drawable.sd_card;
		} else if (file.getPath().toLowerCase().equals(DOWNLOAD_FOLDER)) {
			id = R.drawable.folder_downloads;
		} else if (file.isDirectory()) {
			id = R.drawable.folder;
		} else { // f is file
			type = getMimeType(file);
			ext = FileUtils.getFileExtension(file);

			if (type == null) {
				id = R.drawable.file;
			} else if (type.contains("audio")) {
				id = R.drawable.file_audio;
			} else if (type.contains("video")) {
				id = R.drawable.file_video;
			} else if (type.contains("xml")) {
				id = R.drawable.file_xml;
			} else if (type.contains("text")) {
				id = R.drawable.file_text;
			} else if (type.startsWith("application")) {
				if (type.equals("application/vnd.android.package-archive")) {
					id = R.drawable.file_apk;
				} else if (type.equals("application/x-compressed")) {
					id = R.drawable.file_compressed;
				} else if (type.equals("application/zip")) {
					id = R.drawable.file_compressed;
				} else if (type.equals("application/pdf")) {
					id = R.drawable.file_pdf;
				} else if (ext.endsWith("db")) {
					id = R.drawable.file_db;
				} else {
					id = R.drawable.file_app;
				}
			} else if (type.contains("image")) {
				id = R.drawable.file_image;
			} else {
				id = R.drawable.file;
			}
		}

		if (priv) {
			id = getPrivateDrawable(id);
		} else if (locked) {
			id = getLockedDrawable(id);
		}

		return id;
	}

	protected static int getLockedDrawable(int id) {
		int res = id;
		if (id == R.drawable.file) {
			res = R.drawable.file_locked;
		} else if (id == R.drawable.folder) {
			res = R.drawable.folder_locked;
		} else if (id == R.drawable.file_text) {
			res = R.drawable.file_text_locked;
		} else if (id == R.drawable.file_audio) {
			res = R.drawable.file_audio_locked;
		} else if (id == R.drawable.file_video) {
			res = R.drawable.file_video_locked;
		} else if (id == R.drawable.file_image) {
			res = R.drawable.file_image_locked;
		} else if (id == R.drawable.file_apk) {
			res = R.drawable.file_apk_locked;
		} else if (id == R.drawable.file_app) {
			res = R.drawable.file_app_locked;
		} else if (id == R.drawable.file_pdf) {
			res = R.drawable.file_pdf_locked;
		} else if (id == R.drawable.file_db) {
			res = R.drawable.file_db_locked;
		} else if (id == R.drawable.file_compressed) {
			res = R.drawable.file_compressed_locked;
		}

		return res;
	}

	protected static int getPrivateDrawable(int id) {
		int res = id;
		if (id == R.drawable.file) {
			res = R.drawable.file_private;
		} else if (id == R.drawable.folder) {
			res = R.drawable.folder_private;
		} else if (id == R.drawable.file_text) {
			res = R.drawable.file_text_private;
		} else if (id == R.drawable.file_audio) {
			res = R.drawable.file_audio_private;
		} else if (id == R.drawable.file_video) {
			res = R.drawable.file_video_private;
		} else if (id == R.drawable.file_image) {
			res = R.drawable.file_image_private;
		} else if (id == R.drawable.file_apk) {
			res = R.drawable.file_apk_private;
		} else if (id == R.drawable.file_app) {
			res = R.drawable.file_app_private;
		} else if (id == R.drawable.file_pdf) {
			res = R.drawable.file_pdf_private;
		} else if (id == R.drawable.file_db) {
			res = R.drawable.file_db_private;
		} else if (id == R.drawable.file_compressed) {
			res = R.drawable.file_compressed_private;
		}
		return res;
	}

	/**
	 * @param folder
	 *            the current parent folder for displayed files
	 */
	public void setCurrentFolder(File folder) {
		mFolder = folder;
	}

	/**
	 * @param iconOnTop
	 *            let the icon be above the file name
	 */
	public void setIconOnTop(boolean iconOnTop) {
		mIconOnTop = iconOnTop;
	}

	/**
	 * @param provider
	 *            the {@link ThumbnailProvider} for this adapter
	 * 
	 */
	public void setThumbnailProvider(ThumbnailProvider provider) {
		mThumbnailProvider = provider;
	}

	/**
	 * @param selection
	 *            the list of selected files
	 */
	public void setSelection(List<File> selection) {
		mSelection = selection;
	}

	protected int mLayout;
	protected boolean mIconOnTop;

	protected File mFolder;

	protected ThumbnailProvider mThumbnailProvider;

	protected List<File> mSelection;
}
