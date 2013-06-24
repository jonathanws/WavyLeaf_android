package com.towson.wavyleaf;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LoginContinued extends SherlockActivity {
	
	TextView createAccount;
	Spinner education, experience, confidence_plant, confidence_wavyleaf;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.layout_login_continued);
		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.menu_login_continued, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent loginIntent = new Intent(this, Login.class);
				loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(loginIntent);
				finish();
				return true;
			case R.id.menu_createaccount:
	        	submit();
	        	uploadData();
	        	Toast.makeText(getApplicationContext(), "Account Details Recorded", Toast.LENGTH_SHORT).show();
	        	Intent mainIntent = new Intent(this, Main.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mainIntent);
				finish();
	        	return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		createAccount = (TextView) findViewById(R.id.login_continued_tv_createaccount);
		education = (Spinner) findViewById(R.id.login_education);
		experience = (Spinner) findViewById(R.id.login_experience);
		confidence_plant = (Spinner) findViewById(R.id.login_confidence_plant);
		confidence_wavyleaf = (Spinner) findViewById(R.id.login_confidence_wavyleaf);
		
		createAccount.setTypeface(tf_light);
		
		// Anywhere you see "array" (what the user picks from for education/confidence etc) these values are in arrays.xml, near the bottom
		
		// For Education
		ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(this, R.array.education_array, android.R.layout.simple_spinner_item);
		educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		education.setAdapter(educationAdapter);
		
		// For Outdoor Experience
		ArrayAdapter<CharSequence> experienceAdapter = ArrayAdapter.createFromResource(this, R.array.experience_array, android.R.layout.simple_spinner_item);
		experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		experience.setAdapter(experienceAdapter);
		
		// For confidence in general plant ID
		ArrayAdapter<CharSequence> confidence_plantAdapter = ArrayAdapter.createFromResource(this, R.array.confidence_plant_array, android.R.layout.simple_spinner_item);
		confidence_plantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		confidence_plant.setAdapter(confidence_plantAdapter);
		
		// For confidence in wavyleaf ID
		ArrayAdapter<CharSequence> confidence_wavyleafAdapter = ArrayAdapter.createFromResource(this, R.array.confidence_wavyleaf_array, android.R.layout.simple_spinner_item);
		confidence_wavyleafAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		confidence_wavyleaf.setAdapter(confidence_wavyleafAdapter);
	}
	
	protected void submit() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		
		ed.putString("KEY_EDUCATION", education.getSelectedItem() + "");
		ed.putString("KEY_EXPERIENCE", experience.getSelectedItem() + "");
		ed.putString("KEY_CONFIDENCE_PLANT", confidence_plant.getSelectedItem() + "");
		ed.putString("KEY_CONFIDENCE_WAVYLEAF", confidence_wavyleaf.getSelectedItem() + "");
		ed.commit();
	}
	
	protected void uploadData() {
		JSONObject json = new JSONObject();
		try {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			json.put(UploadData.ARG_NAME, sp.getString(Settings.KEY_NAME, "null"));
			json.put(UploadData.ARG_BIRTHYEAR, sp.getString(Settings.KEY_BIRTHYEAR, "null"));
			json.put(UploadData.ARG_EDUCATION, education.getSelectedItem());
			json.put(UploadData.ARG_OUTDOOREXPERIENCE, experience.getSelectedItem());
			json.put(UploadData.ARG_GENERALPLANTID, confidence_plant.getSelectedItem());
			json.put(UploadData.ARG_WAVYLEAFID, confidence_wavyleaf.getSelectedItem());
			json.put(UploadData.ARG_EMAIL, sp.getString(Settings.KEY_EMAIL, "null"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		new UploadData(this, UploadData.TASK_SUBMIT_USER).execute(json);
	}

}
