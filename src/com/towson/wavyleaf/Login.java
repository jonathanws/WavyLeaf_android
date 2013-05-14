package com.towson.wavyleaf;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.towson.wavyleaf.R.id;

public class Login extends SherlockActivity {
	
	TextView createAccount, anon;
	EditText name, year;
	CheckBox cb;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.layout_login);
		init();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getSupportMenuInflater().inflate(R.menu.menu_login, menu);
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
			case R.id.menu_next:
				if (hasText()) {
					submit();
					Intent nextIntent = new Intent(this, LoginContinued.class);
					nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(nextIntent);
					overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_left);
					uploadData();
					finish();
					return true;
				}
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		createAccount = (TextView) findViewById(R.id.login_tv_createaccount);
		anon = (TextView) findViewById(id.tv_anon);
		name = (EditText) findViewById(R.id.login_name);
		year = (EditText) findViewById(R.id.login_birthyear);
		cb = (CheckBox) findViewById(R.id.login_cb);
		
		createAccount.setTypeface(tf_light);
		anon.setTypeface(tf_light);
		
		// Listener for Checkbox
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked == true)
					name.setText("Anonymous");
				else
					name.setText("");
			}
		});
		
	}
	
	// Verify existing text, and verify not only spaces
	protected boolean hasText() {
		if (!name.getText().toString().trim().equals(""))
			return true;
		else {
			Toast.makeText(getApplicationContext(), "Please complete both fields", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	protected void submit() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		
		ed.putString(Settings.KEY_NAME, name.getText().toString().trim());
		ed.putString(Settings.KEY_BIRTHYEAR, year.getText() + "");
		ed.commit();
	}
	
	protected void uploadData() {
		JSONObject json = new JSONObject();
		try {
			json.put(UploadData.ARG_NAME, name.getText().toString().trim());
			json.put(UploadData.ARG_BIRTHYEAR, year.getText().toString());
			json.put(UploadData.ARG_EDUCATION, "lol");
			json.put(UploadData.ARG_OUTDOOREXPERIENCE, "lol");
			json.put(UploadData.ARG_GENERALPLANTID, "lol");
			json.put(UploadData.ARG_WAVYLEAFID, "lol");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		new UploadData(this, UploadData.TASK_SUBMIT_USER).execute(json);
	}
	
}
