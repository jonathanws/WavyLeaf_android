package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class Splash extends SherlockActivity {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		// If user wants a splash screen
		if (sp.getBoolean(Settings.KEY_SPLASH, true)) {
			setContentView(R.layout.layout_splash);
			init();
			runSplash();
		} else
			goTo(Login.class);
	}
	
	private void runSplash() {
		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				} finally {
					goTo(Login.class);
				}
			}
		};
		timer.start();
	}
	
	private void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");	
		TextView tv = (TextView) findViewById(R.id.splash_textview);
		tv.setTypeface(tf_light);
	}
	
	protected void goTo(Class<?> uri) {
		Intent intent = new Intent(Splash.this, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

}
