package org.onebeartoe.pixel.android;

import org.onebeartoe.pixel.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * This is the Splash activity which is loaded when the application is invoked,
 * it just displays the PIXEL logo
 */
public class SplashActivity extends Activity {
	// Set the display time, in milliseconds (or extract it out as a
	// configurable parameter)
	private final int SPLASH_DISPLAY_LENGTH = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}

	@Override
	protected void onResume() {
		super.onResume();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// Finish the splash activity so it can't be returned to.
				SplashActivity.this.finish();
				// Create an Intent that will start the main activity.
				Intent mainIntent = new Intent(SplashActivity.this,
						PixelActivity.class);
				SplashActivity.this.startActivity(mainIntent);
			}
		}, SPLASH_DISPLAY_LENGTH);

	}
}