package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Login extends SherlockActivity {
	
	TextView createAccount;
	EditText name, day, year;
	Spinner month;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
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
        case R.id.menu_createaccount:
        	submit();
        	finish();
        	return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		createAccount = (TextView) findViewById(R.id.login_tv_createaccount);
		name = (EditText) findViewById(R.id.login_tv_name);
		day = (EditText) findViewById(R.id.login_tv_day);
		year = (EditText) findViewById(R.id.login_tv_year);
		month = (Spinner) findViewById(R.id.login_sp_month);
		
		createAccount.setTypeface(tf_light);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.month_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		month.setAdapter(adapter);
	}
	
	protected void submit() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		
		ed.putString(Settings.KEY_NAME, name.getText() + "");
		ed.putString(Settings.KEY_DOB, dateOfBirth(
				month.getSelectedItem().toString(), 
				day.getText() + "", 
				year.getText() + ""));
		ed.commit();
	}
	
	protected String dateOfBirth(String month, String day, String year) {
		String dob = "";
		dob = month + " - " + day + " - " + year;		
		return dob;
	}

}
