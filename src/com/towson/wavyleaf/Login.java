package com.towson.wavyleaf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
	
	protected String[] globalArray;
	public static final int EMAIL = 1;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_login);
		init();
		
		// If a user has done this all already, proceed to main
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if (!((sp.getBoolean(Settings.FIRST_RUN, true)) || (sp.getString(Settings.KEY_NAME, "null")) == "null")) {
			Intent mainIntent = new Intent(this, Main.class);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mainIntent);
			finish();
		} else
			showDialog(EMAIL); // Calls onCreateDialog()
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
	
	// This list dialog pops up and offers users to use any of the email addresses already found on their device
	// It is not required to use any pre-existing emails
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case EMAIL:
				return new AlertDialog.Builder(this).setItems(seeAccounts(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						email.setText(globalArray[which]);
					}
				})
				.setTitle("Select email to use")
				.setNegativeButton("None of these", null)
				.create();
		}
		return super.onCreateDialog(id);
		//http://stackoverflow.com/questions/3326366/what-context-should-i-use-alertdialog-builder-in
	}
	
	protected void init() {
		setTitle("Create Account");
		// Set beautiful typefaces
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		createAccount = (TextView) findViewById(R.id.login_tv_createaccount);
		description = (TextView) findViewById(R.id.login_description);
		name = (EditText) findViewById(R.id.login_name);
		year = (EditText) findViewById(R.id.login_birthyear);
		email = (EditText) findViewById(R.id.login_email);
		
		createAccount.setTypeface(tf_light);
		description.setTypeface(tf_light);
	}
	
	/** Verify all fields have text, and verify not only spaces **/
	protected boolean hasText() {
		if (!name.getText().toString().trim().equals("")
				&& (validYear(year.getText().toString()) == true)
				&& (validEmail(email.getText().toString()) == true))
			return true;
		else {
			Toast.makeText(getApplicationContext(), "Please complete all fields with realistic values", Toast.LENGTH_SHORT).show();
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
		else if ((s.length() < 4) || Integer.parseInt(s) < 1900)				// or not an actual year
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
	
	// I can't get the freakin' loop to work with an array... so fine. We'll do it the hard way
	protected String[] seeAccounts() {
		Account[] accounts = AccountManager.get(this).getAccounts();
		String all = null;
		
		// Can't get loop to return array, so pull everything as a gigantic, unreasonable string
		for (Account account : accounts) {
			if (account.toString().contains("@")) {
				all = all + "," + account.name;
			}
		}
		
		// Sometimes this gives us a null, so let's take that out
		if (all.startsWith("null"))
			all = all.substring(5, all.length());
		
		// We have a huge-ass string, so make it an array, then a list, then HashSet.
		ArrayList<String> al = new ArrayList<String>(Arrays.asList(all.split(",")));
		HashSet<String> h = new HashSet<String>(al);
		al.clear();
		al.addAll(h);
		
		globalArray = new String[al.size()];
		globalArray = al.toArray(globalArray);
		
		return globalArray;
		// Ya crybaby
	}
	
}


