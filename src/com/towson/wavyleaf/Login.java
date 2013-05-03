package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.towson.wavyleaf.R.id;

public class Login extends SherlockActivity {
	
	TextView createAccount, anon;
	EditText name, year;
	Spinner education, experience, confidence_plant, confidence_wavyleaf;
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
        case R.id.menu_createaccount:
        	submit();
        	finish();
        	Toast.makeText(getApplicationContext(), "Account Details Recorded", Toast.LENGTH_SHORT).show();
        	return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
		
		createAccount = (TextView) findViewById(R.id.login_tv_createaccount);
		anon = (TextView) findViewById(id.tv_anon);
		name = (EditText) findViewById(R.id.login_name);
		year = (EditText) findViewById(R.id.login_birthyear);
		education = (Spinner) findViewById(R.id.login_education);
		experience = (Spinner) findViewById(R.id.login_experience);
		confidence_plant = (Spinner) findViewById(R.id.login_confidence_plant);
		confidence_wavyleaf = (Spinner) findViewById(R.id.login_confidence_wavyleaf);
		cb = (CheckBox) findViewById(R.id.login_cb);
		
		createAccount.setTypeface(tf_light);
		anon.setTypeface(tf_light);
		
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
	
	protected void submit() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		
		ed.putString(Settings.KEY_NAME, name.getText() + "");
		ed.putString(Settings.KEY_BIRTHYEAR, year.getText() + "");
		ed.putString("KEY_EDUCATION", education.getSelectedItem() + "");
		ed.putString("KEY_EXPERIENCE", experience.getSelectedItem() + "");
		ed.putString("KEY_CONFIDENCE_PLANT", confidence_plant.getSelectedItem() + "");
		ed.putString("KEY_CONFIDENCE_WAVYLEAF", confidence_wavyleaf.getSelectedItem() + "");
		ed.commit();
	}
	
}
