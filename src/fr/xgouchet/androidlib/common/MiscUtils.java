package fr.xgouchet.androidlib.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;
import fr.xgouchet.androidlib.R;

/**
 * 
 */
public final class MiscUtils {

	/**
	 * Start an email composer to send an email
	 * 
	 * @param ctx
	 *            the current context
	 * @param object
	 *            the title of the mail to compose
	 */
	public static void sendEmail(final Context ctx, final CharSequence object) {

		String uriText = "mailto:"
				+ ctx.getResources().getString(R.string.ui_mail) + "?subject="
				+ Uri.encode(object.toString());

		Intent email = new Intent(Intent.ACTION_SENDTO);
		email.setData(Uri.parse(uriText));

		ctx.startActivity(Intent.createChooser(email,
				ctx.getString(R.string.ui_choose_mail)));
	}

	/**
	 * Open the market on my apps
	 * 
	 * @param activity
	 *            the calling activity
	 */
	public static void openMarket(final Activity activity) {
		String url;
		Intent market;
		market = new Intent(Intent.ACTION_VIEW);
		url = activity.getString(R.string.ui_market_url);
		market.setData(Uri.parse(url));
		try {
			activity.startActivity(market);
		} catch (ActivityNotFoundException e) {
			Crouton.showText(activity, R.string.toast_no_market, Style.ALERT);
		}
	}

	/**
	 * Open the market on this app
	 * 
	 * @param activity
	 *            the calling activity
	 * @param appPackage
	 *            the application package name
	 */
	public static void openMarketApp(final Activity activity,
			final CharSequence appPackage) {
		String url;
		Intent market;
		market = new Intent(Intent.ACTION_VIEW);
		url = activity.getString(R.string.ui_market_app_url, appPackage);
		market.setData(Uri.parse(url));
		try {
			activity.startActivity(market);
		} catch (ActivityNotFoundException e) {
			Crouton.showText(activity, R.string.toast_no_market, Style.ALERT);
		}
	}

	/**
	 * Open the market on my apps
	 * 
	 * @param activity
	 *            the calling activity
	 */
	public static void openDonate(final Activity activity) {
		String url;
		Intent donate;
		donate = new Intent(Intent.ACTION_VIEW);
		url = activity.getString(R.string.ui_donate_url);
		donate.setData(Uri.parse(url));
		activity.startActivity(donate);
	}

	private MiscUtils() {
	}
}
