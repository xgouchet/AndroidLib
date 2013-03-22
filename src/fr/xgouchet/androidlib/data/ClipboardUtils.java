package fr.xgouchet.androidlib.data;

import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

public final class ClipboardUtils {

	/**
	 * A basic interface for Clipboard
	 */
	public interface ClipboardProxy {
		/**
		 * @return if any text content can be fetched from the clipboard
		 */
		public boolean hasTextContent();

		/**
		 * @return the text fetched from the clipboard
		 */
		public String[] getText();

		/**
		 * @param text
		 *            the text to put in clipboard
		 * @param label
		 *            an optional label
		 */
		public void setText(String text, String label);

	}

	/**
	 * A proxy for the new Clipboard manager
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static final class ClipboardProxyV11 implements ClipboardProxy {

		public ClipboardProxyV11(final Context context) {
			mManager = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
		}

		@Override
		public boolean hasTextContent() {
			boolean res;

			if (mManager.hasPrimaryClip()) {
				ClipDescription desc = mManager.getPrimaryClipDescription();
				res = desc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
			} else {
				res = false;
			}

			return res;
		}

		@Override
		public String[] getText() {
			ClipData.Item item;
			List<String> content = new LinkedList<String>();
			ClipData data = mManager.getPrimaryClip();
			CharSequence strData;

			if (data != null) {
				int count = data.getItemCount();
				for (int i = 0; i < count; ++i) {
					item = data.getItemAt(i);

					strData = item.getText();
					if (strData != null) {
						content.add(strData.toString());
						continue;
					}
				}
			}

			return content.toArray(new String[content.size()]);
		}

		@Override
		public void setText(final String text, final String label) {
			mManager.setPrimaryClip(ClipData.newPlainText(label, text));
		}

		private final android.content.ClipboardManager mManager;
	}

	/**
	 * A proxy for the old Clipboard manager
	 */
	@SuppressWarnings("deprecation")
	public static final class ClipboardProxyV1 implements ClipboardProxy {

		public ClipboardProxyV1(final Context context) {
			mManager = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
		}

		@Override
		public boolean hasTextContent() {
			return mManager.hasText();
		}

		@Override
		public String[] getText() {
			return new String[] { mManager.getText().toString() };
		}

		@Override
		public void setText(final String text, final String label) {
			mManager.setText(text);
		}

		private final android.text.ClipboardManager mManager;
	}

	public static ClipboardProxy getClipboardProxy(final Context context) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			return new ClipboardProxyV11(context);
		} else {
			return new ClipboardProxyV1(context);
		}
	}
}
