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
public class MiscUtils {

	/**
	 * Start an email composer to send an email
	 * 
	 * @param ctx
	 *            the current context
	 * @param object
	 *            the title of the mail to compose
	 */
	public static void sendEmail(Context ctx, CharSequence object) {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("text/plain");
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { ctx.getResources()
				.getString(R.string.ui_mail) });
		email.putExtra(Intent.EXTRA_SUBJECT, object);
		ctx.startActivity(Intent.createChooser(email,
				ctx.getString(R.string.ui_choose_mail)));
	}

	/**
	 * Open the market on my apps
	 * 
	 * @param activity
	 *            the calling activity
	 */
	public static void openMarket(Activity activity) {
		String url;
		Intent market = new Intent(Intent.ACTION_VIEW);
		// market.setData(Uri.parse("market://search?q=pub:Xavier Gouchet"));
		url = activity.getResources().getString(R.string.ui_market_url);
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
	public static void openMarketApp(Activity activity, CharSequence appPackage) {
		String url;
		Intent market = new Intent(Intent.ACTION_VIEW);
		url = activity.getResources().getString(R.string.ui_market_app_url,
				appPackage);
		market.setData(Uri.parse(url));
		try {
			activity.startActivity(market);
		} catch (ActivityNotFoundException e) {
			Crouton.showText(activity, R.string.toast_no_market, Style.ALERT);
		}
	}
}
