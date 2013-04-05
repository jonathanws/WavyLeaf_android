package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockActivity implements OnClickListener {
	
	private static final int HELP = 0;
	protected Button bu_new, bu_session;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_main);
		initLayout();
	}
	
	protected void initLayout() {
		bu_new = (Button) findViewById(R.id.button_new);
		bu_session = (Button) findViewById(R.id.button_session);
		bu_new.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_help:
				showDialog(HELP);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View view) {
		if (view == this.bu_new) {
			Intent newReportIntent = new Intent(this, Report.class);
			this.startActivity(newReportIntent);
			
		} //else if (view == this.bu_upload) {
//			Intent uploadIntent = new Intent(this, Upload.class);
//			this.startActivity(uploadIntent);
//		} else if (view == this.bu_session) {
//			Intent sessionIntent = new Intent(this, Session.class);
//			this.startActivity(sessionIntent);
//		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case HELP:
				return new AlertDialog.Builder(this)
				.setTitle("HALP")
				.setMessage("I'm here to help!")
				.setPositiveButton("Phew!", null)
				.setNegativeButton("cancel", null)
				.create();
		}
		return super.onCreateDialog(id);
	}

}
