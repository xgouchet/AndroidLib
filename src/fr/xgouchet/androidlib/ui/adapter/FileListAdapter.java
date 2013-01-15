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
	public FileListAdapter(final Context context, final List<File> objects,
			final File folder) {
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
	public FileListAdapter(final Context context, final List<File> objects,
			final File folder, final int layout) {
		super(context, layout, objects);
		mLayout = layout;
		mFolder = folder;
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * @see ArrayAdapter#getView(int, View, ViewGroup)
	 */
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		File file;
		View view;
		TextView textView;
		int style;

		// recycle the view
		view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_file, null);
		}

		// get the file infos
		file = getItem(position);

		final int icon = getIconForItem(file, position);
		final String text = getTextForItem(file, position);
		final Drawable thumbnail = getThumbnail(file, icon);
		final int size = resizeThumbnail(thumbnail);
		Drawable selectedIcon = null;

		// Setup name and icon
		textView = (TextView) view.findViewById(R.id.textFileName);
		if (textView != null) {

			// Handle selection
			int color;
			if ((mSelection != null) && (mSelection.contains(file))) {
				selectedIcon = getContext().getResources().getDrawable(
						R.drawable.selected);
				selectedIcon.setBounds(0, 0, size, size);
				style = Typeface.BOLD_ITALIC;
				color = Color.rgb(0, 192, 0);
			} else {
				color = Color.LTGRAY;
				style = Typeface.BOLD;
			}

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

		return view;
	}

	protected int getIconForItem(final File file, final int position) {
		int icon;
		if (file == null) {
			icon = R.drawable.file_unknown;
		} else {
			if ((position == 0) && (mFolder != null)
					&& (file.equals(mFolder.getParentFile()))
					&& (!mFolder.getPath().equals("/"))) {
				icon = R.drawable.up;
			} else {
				if (FileUtils.isSymLink(file)) {
					final File target = FileUtils.getSymLinkTarget(file);
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
		}
		return icon;
	}

	protected String getTextForItem(final File file, final int position) {
		String text;
		if (file == null) {
			text = "";
		} else {
			if ((position == 0) && (mFolder != null)
					&& (file.equals(mFolder.getParentFile()))
					&& (!mFolder.getPath().equals("/"))) {
				text = "";
			} else {
				text = file.getName();
			}
		}
		return text;
	}

	protected Drawable getThumbnail(final File file, final int icon) {
		Drawable thumbnail = null;

		// File Icon / Thumbnail
		if (mProvider != null) {
			thumbnail = mProvider.getThumbnailForFile(getContext(), file);
		}
		if (thumbnail == null) {
			thumbnail = getContext().getResources().getDrawable(icon);
		}

		return thumbnail;
	}

	protected int resizeThumbnail(final Drawable thumbnail) {
		int size;
		size = UIUtils.getPxFromDp(getContext(), 38);

		if (thumbnail != null) {
			double ratio;
			ratio = ((double) thumbnail.getIntrinsicWidth())
					/ ((double) thumbnail.getIntrinsicHeight());
			if (ratio > 1) {
				thumbnail.setBounds(0, 0, size, (int) (size / ratio));
			} else {
				thumbnail.setBounds(0, 0, (int) (size * ratio), size);
			}
		}
		return size;
	}

	public static int getIconForFile(final File file) {
		int resId;
		boolean locked, priv;
		String type, ext;

		priv = file.canRead() ^ true;
		locked = file.canRead() && !file.canWrite();

		if (file.getPath().equals(STORAGE_PATH)) { // External storage
			resId = R.drawable.sd_card;
		} else if (file.getPath().equalsIgnoreCase(DOWNLOAD_FOLDER)) {
			resId = R.drawable.folder_downloads;
		} else if (file.isDirectory()) {
			resId = R.drawable.folder;
		} else { // f is file
			type = getMimeType(file);
			ext = FileUtils.getFileExtension(file);

			if (type == null) {
				resId = R.drawable.file;
			} else if (type.contains("audio")) {
				resId = R.drawable.file_audio;
			} else if (type.contains("video")) {
				resId = R.drawable.file_video;
			} else if (type.contains("xml")) {
				resId = R.drawable.file_xml;
			} else if (type.contains("text")) {
				resId = R.drawable.file_text;
			} else if (type.startsWith("application")) {
				if ("application/vnd.android.package-archive".equals(type)) {
					resId = R.drawable.file_apk;
				} else if ("application/x-compressed".equals(type)) {
					resId = R.drawable.file_compressed;
				} else if ("application/zip".equals(type)) {
					resId = R.drawable.file_compressed;
				} else if ("application/pdf".equals(type)) {
					resId = R.drawable.file_pdf;
				} else if (ext.endsWith("db")) {
					resId = R.drawable.file_db;
				} else {
					resId = R.drawable.file_app;
				}
			} else if (type.contains("image")) {
				resId = R.drawable.file_image;
			} else {
				resId = R.drawable.file;
			}
		}

		if (priv) {
			resId = getPrivateDrawable(resId);
		} else if (locked) {
			resId = getLockedDrawable(resId);
		}

		return resId;
	}

	protected static int getLockedDrawable(final int resId) {
		int res;
		if (resId == R.drawable.file) {
			res = R.drawable.file_locked;
		} else if (resId == R.drawable.folder) {
			res = R.drawable.folder_locked;
		} else if (resId == R.drawable.file_text) {
			res = R.drawable.file_text_locked;
		} else if (resId == R.drawable.file_audio) {
			res = R.drawable.file_audio_locked;
		} else if (resId == R.drawable.file_video) {
			res = R.drawable.file_video_locked;
		} else if (resId == R.drawable.file_image) {
			res = R.drawable.file_image_locked;
		} else if (resId == R.drawable.file_apk) {
			res = R.drawable.file_apk_locked;
		} else if (resId == R.drawable.file_app) {
			res = R.drawable.file_app_locked;
		} else if (resId == R.drawable.file_pdf) {
			res = R.drawable.file_pdf_locked;
		} else if (resId == R.drawable.file_db) {
			res = R.drawable.file_db_locked;
		} else if (resId == R.drawable.file_compressed) {
			res = R.drawable.file_compressed_locked;
		} else {
			res = resId;
		}

		return res;
	}

	protected static int getPrivateDrawable(final int resId) {
		int res;
		if (resId == R.drawable.file) {
			res = R.drawable.file_private;
		} else if (resId == R.drawable.folder) {
			res = R.drawable.folder_private;
		} else if (resId == R.drawable.file_text) {
			res = R.drawable.file_text_private;
		} else if (resId == R.drawable.file_audio) {
			res = R.drawable.file_audio_private;
		} else if (resId == R.drawable.file_video) {
			res = R.drawable.file_video_private;
		} else if (resId == R.drawable.file_image) {
			res = R.drawable.file_image_private;
		} else if (resId == R.drawable.file_apk) {
			res = R.drawable.file_apk_private;
		} else if (resId == R.drawable.file_app) {
			res = R.drawable.file_app_private;
		} else if (resId == R.drawable.file_pdf) {
			res = R.drawable.file_pdf_private;
		} else if (resId == R.drawable.file_db) {
			res = R.drawable.file_db_private;
		} else if (resId == R.drawable.file_compressed) {
			res = R.drawable.file_compressed_private;
		} else {
			res = resId;
		}
		return res;
	}

	/**
	 * @param folder
	 *            the current parent folder for displayed files
	 */
	public void setCurrentFolder(final File folder) {
		mFolder = folder;
	}

	/**
	 * @param iconOnTop
	 *            let the icon be above the file name
	 */
	public void setIconOnTop(final boolean iconOnTop) {
		mIconOnTop = iconOnTop;
	}

	/**
	 * @param provider
	 *            the {@link ThumbnailProvider} for this adapter
	 * 
	 */
	public void setThumbnailProvider(final ThumbnailProvider provider) {
		mProvider = provider;
	}

	/**
	 * @param selection
	 *            the list of selected files
	 */
	public void setSelection(final List<File> selection) {
		mSelection = selection;
	}

	protected final LayoutInflater mInflater;
	protected int mLayout;
	protected boolean mIconOnTop;

	protected File mFolder;
	protected ThumbnailProvider mProvider;
	protected List<File> mSelection;
}
