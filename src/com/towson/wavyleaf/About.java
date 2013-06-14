package com.towson.wavyleaf;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.LinearLayout;

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
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll_about);
		
		// Set typeface to every TextView
		for (int i = 0; i < ll.getChildCount(); i++) {
			View child = ll.getChildAt(i);
			
			if (child instanceof TextView)
				((TextView) child).setTypeface(tf_light);
		}
	}
	
}
