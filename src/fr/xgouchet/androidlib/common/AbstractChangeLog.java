package fr.xgouchet.androidlib.common;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import fr.xgouchet.androidlib.R;

/**
 *  
 */
public abstract class AbstractChangeLog {

	/**
	 * 
	 * @param context
	 * @param prefs
	 * @return if an update was detected
	 */
	public boolean displayChangeLog(final Context context,
			final SharedPreferences prefs) {

		boolean updateLaunch;
		updateLaunch = isFirstLaunchAfterUpdate(context, prefs);

		if (updateLaunch) {
			displayUpdateDialog(context);
		}

		saveCurrentVersion(context, prefs);

		return updateLaunch;
	}

	/**
	 * Displays an alert dialog with the update info
	 * 
	 * @param context
	 *            the current application context
	 */
	protected void displayUpdateDialog(Context context) {
		Builder builder;

		builder = new Builder(context);

		builder.setTitle(R.string.ui_whats_new);
		builder.setMessage(getChangelogMessage(context));
		builder.setCancelable(true);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	/**
	 * @param context
	 *            the current application context
	 * @return the message for the changelog
	 */
	protected String getChangelogMessage(Context context) {
		StringBuilder builder = new StringBuilder();

		builder.append(context.getString(getTitleResource(context)));
		builder.append("\n\n");
		builder.append(context.getString(getChangeLogResource(context)));

		return builder.toString();
	}

	/**
	 * @param version
	 *            the version code of the application
	 * @return the string resource for the title to display in the change log
	 */
	public abstract int getTitleResourceForVersion(int version);

	/**
	 * @param context
	 *            the current application context
	 * @return the string resource for the title to display in the change log
	 */
	public int getTitleResource(final Context context) {
		return getTitleResourceForVersion(getCurrentVersion(context));
	}

	/**
	 * @param version
	 *            the version code of the application
	 * @return the string resource for the changelog to display in the change
	 *         log
	 */
	public abstract int getChangeLogResourceForVersion(int version);

	/**
	 * @param context
	 *            the current application context
	 * @return the string resource for the changelog to display in the change
	 *         log
	 */
	public int getChangeLogResource(final Context context) {
		return getChangeLogResourceForVersion(getCurrentVersion(context));
	}

	/**
	 * Warning, this automatically saves the current version, so you only get
	 * one shot. Then again, I guess that's the idea...
	 * 
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences to use
	 * @return if this is the first launch since last update
	 */
	public boolean isFirstLaunchAfterUpdate(final Context context,
			final SharedPreferences prefs) {
		int previous, current;

		previous = getPreviousVersion(context, prefs);
		current = getCurrentVersion(context);

		return (previous < current);
	}

	/**
	 * Warning, this automatically saves the current version, so you only get
	 * one shot. Then again, I guess that's the idea...
	 * 
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences to use
	 * @return if this is the first launch since last update
	 */
	public boolean isFirstLaunchEver(final Context context,
			final SharedPreferences prefs) {
		int previous;

		previous = getPreviousVersion(context, prefs);

		return (previous < 0);
	}

	/**
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences to use
	 * @return the previously opened version of the application
	 */
	protected int getPreviousVersion(final Context context,
			final SharedPreferences prefs) {
		return prefs.getInt(PREF_PREV_VERSION, -1);
	}

	/**
	 * @param context
	 *            the current application context
	 * @return the current installed application version
	 */
	protected int getCurrentVersion(final Context context) {
		int version;

		if (mVersion < 0) {
			try {
				version = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				Log.e("Felix", "Unable to get package info for package name "
						+ context.getPackageName());
				version = -1;
			}

			if (version >= 0) {
				mVersion = version;
			}
		} else {
			version = mVersion;
		}

		return version;
	}

	/**
	 * Saves the current version for next launch
	 * 
	 * @param context
	 *            the current application context
	 * @param prefs
	 *            the shared preferences for this app
	 */
	public void saveCurrentVersion(final Context context,
			final SharedPreferences prefs) {
		Editor editor;

		editor = prefs.edit();
		editor.putInt(PREF_PREV_VERSION, getCurrentVersion(context));
		editor.commit();
	}

	private static final String PREF_PREV_VERSION = "previous_version";
	private int mVersion = -1;
}
