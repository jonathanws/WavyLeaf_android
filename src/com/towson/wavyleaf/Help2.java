package com.towson.wavyleaf;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class Help2 extends SherlockActivity {
	
	TextView how, iden, faq;
	
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_help2);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		init();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        	Intent mainIntent = new Intent(this, Main.class);
        	mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(mainIntent);
        	finish();
        	return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	protected void init() {
		setTitle("Help");
		// Set beautiful typefaces
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		how = (TextView) findViewById(R.id.help_tv_howthisworks);
		iden = (TextView) findViewById(R.id.help_tv_identification);
		faq = (TextView) findViewById(R.id.help_tv_faq);
		
		how.setTypeface(tf_light);
		iden.setTypeface(tf_light);
		faq.setTypeface(tf_light);
	}

}
