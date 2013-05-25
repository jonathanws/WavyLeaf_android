package com.towson.wavyleaf;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.layout_about);
		init();
	}
	
	private void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		
		TextView wavyleaf = (TextView) findViewById(R.id.tv_about_wavyleaf);
		wavyleaf.setTypeface(tf_light);
		TextView version = (TextView) findViewById(R.id.tv_about_version);
		version.setTypeface(tf_light);
		TextView developed = (TextView) findViewById(R.id.tv_about_developed);
		developed.setTypeface(tf_light);
		TextView towson = (TextView) findViewById(R.id.tv_about_towson);
		towson.setTypeface(tf_light);
	}
	
}
