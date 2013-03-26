package com.towson.wavyleaf;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

// TODO: Apparently the code for up navigation doesn't work on jelly bean devices

public class Main extends SherlockActivity implements OnClickListener {
	
	Button bu_new, bu_edit, bu_upload, bu_session;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initLayout();
	}
	
	protected void initLayout() {
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
		bu_new = (Button) findViewById(R.id.button_new);
		bu_edit = (Button) findViewById(R.id.button_edit);
		bu_upload = (Button) findViewById(R.id.button_upload);
		bu_session = (Button) findViewById(R.id.button_session);
		bu_new.setOnClickListener(this);
//		bu_edit.setOnClickListener(this);
//		bu_upload.setOnClickListener(this);
//		bu_session.setOnClickListener(this);
		bu_new.setTypeface(tf);
		bu_edit.setTypeface(tf);
		bu_upload.setTypeface(tf);
		bu_session.setTypeface(tf);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
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
	
	@Override
	public void onClick(View view) {
		if (view == this.bu_new) {
			Intent newReportIntent = new Intent(this, Report.class);
			this.startActivity(newReportIntent);
			
		} //else if (view == this.bu_edit) {
//			Intent EditIntent = new Intent(this, Edit.class);
//			this.startActivity(EditIntent);
//		} else if (view == this.bu_upload) {
//			Intent uploadIntent = new Intent(this, Upload.class);
//			this.startActivity(uploadIntent);
//		} else if (view == this.bu_session) {
//			Intent sessionIntent = new Intent(this, Session.class);
//			this.startActivity(sessionIntent);
//		}
	}

}
