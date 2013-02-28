package fr.xgouchet.androidlib.common;

import android.content.Context;

/**
 * 
 */
public final class UIUtils {

	/**
	 * @param context
	 *            the current application context
	 * @param dp
	 *            the dip value to convert
	 * @return the px value corresponding to the given dip
	 */
	public static int getPxFromDp(final Context context, final int sizeDp) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return ((int) ((sizeDp * scale) + 0.5f));
	}

	private UIUtils() {
	}
}
