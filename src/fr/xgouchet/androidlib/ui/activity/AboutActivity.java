package fr.xgouchet.androidlib.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import fr.xgouchet.androidlib.R;
import fr.xgouchet.androidlib.common.MiscUtils;

/**
 * It must contains two buttons : with the id buttonMail and buttonMarket. The
 * best way is to include the about_generic layout in the layout of children
 * activity
 */
@SuppressLint("Registered")
public class AboutActivity extends Activity implements OnClickListener {

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		findViewById(R.id.buttonMail).setOnClickListener(this);
		findViewById(R.id.buttonMarket).setOnClickListener(this);
		findViewById(R.id.buttonRate).setOnClickListener(this);
		findViewById(R.id.buttonDonate).setOnClickListener(this);
	}

	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(final View view) {

		if (view.getId() == R.id.buttonMail) {
			PackageManager pm = getPackageManager();
			ApplicationInfo appInfo = getApplicationInfo();
			PackageInfo pkgInfo;
			try {
				pkgInfo = pm.getPackageInfo(appInfo.packageName, 0);
			} catch (NameNotFoundException e) {
				pkgInfo = new PackageInfo();
				pkgInfo.versionName = "?";
			}

			CharSequence appName;
			appName = pm.getApplicationLabel(appInfo) + " (version "
					+ pkgInfo.versionName + ")";

			MiscUtils.sendEmail(this, appName);
		} else if (view.getId() == R.id.buttonMarket) {
			MiscUtils.openMarket(this);
		} else if (view.getId() == R.id.buttonRate) {
			CharSequence appPackage;
			appPackage = getApplicationInfo().packageName;
			MiscUtils.openMarketApp(this, appPackage);
		} else if (view.getId() == R.id.buttonDonate) {
			MiscUtils.openDonate(this);
		}
	}

	public void onDonate() {

	}
}
