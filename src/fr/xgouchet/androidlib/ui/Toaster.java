package fr.xgouchet.androidlib.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

@Deprecated
public final class Toaster {

	/**
	 * Show a toast message to the user, ensuring the toast is called from the
	 * main thread
	 * 
	 * @param activity
	 *            The activity to use
	 * @param resId
	 *            the id of the string to display
	 * @param error
	 *            is the message an error (changes the text to red)
	 */
	@Deprecated
	public static void showToastOnMainThread(final Activity activity,
			final int resId, final boolean error) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				showToast(activity, resId, error);
			}
		});
	}

	/**
	 * Show a toast message to the user, ensuring the toast is called from the
	 * main thread
	 * 
	 * @param activity
	 *            The activity to use
	 * @param message
	 *            the string to display
	 * @param error
	 *            is the message an error (changes the text to red)
	 */
	@Deprecated
	public static void showToastOnMainThread(final Activity activity,
			final CharSequence message, final boolean error) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				showToast(activity, message, error);
			}
		});
	}

	/**
	 * Show a toast message to the user
	 * 
	 * @param ctx
	 *            The context to use
	 * @param resId
	 *            the id of the string to display
	 * @param error
	 *            is the message an error (changes the text to red)
	 */
	@Deprecated
	public static void showToast(final Context ctx, final int resId, final boolean error) {
		Toast toast;
		TextView view;

		toast = Toast.makeText(ctx, resId, Toast.LENGTH_SHORT);
		if (error) {
			view = (TextView) toast.getView().findViewById(android.R.id.message);
			view.setTextColor(ERROR_COLOR);
			toast.setDuration(Toast.LENGTH_LONG);
		}
		toast.show();
	}

	/**
	 * Show a toast message to the user
	 * 
	 * @param ctx
	 *            The context to use
	 * @param message
	 *            the string to display
	 * @param error
	 *            is the message an error (changes the text to red)
	 */
	@Deprecated
	public static void showToast(final Context ctx, final CharSequence message,
			final boolean error) {
		Toast toast;
		TextView view;

		toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		if (error) {
			view = (TextView) toast.getView().findViewById(android.R.id.message);
			view.setTextColor(ERROR_COLOR);
			toast.setDuration(Toast.LENGTH_LONG);
		}
		toast.show();
	}

	private Toaster() {
	}

	private static final int ERROR_COLOR = Color.rgb(255, 128, 64);
}
