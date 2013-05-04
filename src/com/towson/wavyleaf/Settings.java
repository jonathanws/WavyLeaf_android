package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class Settings extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_BIRTHYEAR = "preference_birthyear";
	public static final String KEY_CHECKBOX_VIBRATE = "preference_vibrate";
	public static final String KEY_CHECKBOX_NOISE = "preference_noise";
	public static final String KEY_NAME = "preference_name";
	public static final String KEY_USERNAME = "preference_username";
	public static final String KEY_SINGLETALLY = "preference_singletally";
	public static final String KEY_TRIPTALLY = "preference_triptally";
	public static final String KEY_TRIPTALLY_CURRENT = "preference_triptally_current"; // Key for tally for only current trip

	@Deprecated
	@Override
	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		setSummaries();
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
	
	@Deprecated
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Deprecated
	@Override
	protected void onResume() {
		super.onResume();
		setSummaries();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		setSummaries();
	}
	
	@Deprecated
	private void setSummaries() {
		
		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
		
		// Instantiation
		Preference p_age = (Preference) findPreference(KEY_BIRTHYEAR);
		Preference p_name = (Preference) findPreference(KEY_NAME);
		Preference p_username = findPreference(KEY_USERNAME);
		Preference p_tally_single = findPreference(KEY_SINGLETALLY);
		Preference p_tally_trip = findPreference(KEY_TRIPTALLY);
		CheckBoxPreference cbp_vibrate = (CheckBoxPreference) findPreference(KEY_CHECKBOX_VIBRATE);
		CheckBoxPreference cbp_noise = (CheckBoxPreference) findPreference(KEY_CHECKBOX_NOISE);
		
		// Read values
		String string_name = sp.getString(KEY_NAME, "null");
		String string_username = sp.getString(KEY_USERNAME, "null");
		String string_age = sp.getString(KEY_BIRTHYEAR, "null");
		int int_tally_single = sp.getInt(KEY_SINGLETALLY, 0);
		int int_tally_trip = sp.getInt(KEY_TRIPTALLY, 0);
		boolean boolean_vibrate = sp.getBoolean(KEY_CHECKBOX_VIBRATE, true);
		boolean boolean_noise = sp.getBoolean(KEY_CHECKBOX_NOISE, true);
		
		// Set Summaries
		p_name.setSummary(capitalizeFirstLetter(string_name));
		p_username.setSummary(capitalizeFirstLetter(string_username));
		p_age.setSummary(capitalizeFirstLetter(string_age));
		p_tally_single.setSummary(int_tally_single + "");
		p_tally_trip.setSummary(int_tally_trip + "");
		cbp_vibrate.setChecked(boolean_vibrate);
		cbp_noise.setChecked(boolean_noise);
	}
	
	private String capitalizeFirstLetter(String paramString) {
		if (paramString.equalsIgnoreCase(""))
			return "- - -";
		else {
			StringBuilder sb = new StringBuilder(paramString);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			return sb.toString();
		}
	}
	
}