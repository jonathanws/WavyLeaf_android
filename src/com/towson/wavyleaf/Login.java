package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Login extends SherlockActivity {
	
	TextView createAccount, description;
	EditText name, year, email;
	
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
					finish();
					return true;
				}
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		createAccount = (TextView) findViewById(R.id.login_tv_createaccount);
		description = (TextView) findViewById(R.id.login_description);
		name = (EditText) findViewById(R.id.login_name);
		year = (EditText) findViewById(R.id.login_birthyear);
		email = (EditText) findViewById(R.id.login_email);
		
		createAccount.setTypeface(tf_light);
		description.setTypeface(tf_light);
	}
	
	/** Verify existing text, and verify not only spaces **/
	protected boolean hasText() {
		if (!name.getText().toString().trim().equals("")
				&& (validYear(year.getText().toString()) == true)
				&& (validEmail(email.getText().toString()) == true))
			return true;
		else {
			Toast.makeText(getApplicationContext(), "Please complete all fields", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	protected boolean validEmail(String s) {
		if (s.trim().equals(""))				// If blank string
			return false;
		else if (!s.contains("@"))				// or doesn't contain "@"
			return false;
		else
			return true;
	}
	
	protected boolean validYear(String s) {
		if (s.trim().equals(""))				// If blank string
			return false;
		else if (s.length() < 4)				// or not an actual year
			return false;
		else
			return true;
	}
	
	protected void submit() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		
		ed.putString(Settings.KEY_NAME, name.getText().toString().trim());
		ed.putString(Settings.KEY_BIRTHYEAR, year.getText() + "");
		ed.putString(Settings.KEY_EMAIL, email.getText() + "");
		ed.commit();
	}
	
}
