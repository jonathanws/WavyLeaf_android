package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class Settings extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_EDITTEXT_USERNAME = "preference_name";
	public static final String KEY_NAME = "preference_username";
	public static final String KEY_SINGLETALLY = "preference_singletally";
	public static final String KEY_TRIPTALLY = "preference_triptally";

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
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		setSummaries();
	}
	
	@Deprecated
	private void setSummaries() {
		
		SharedPreferences localSharedPreferences = getPreferenceScreen().getSharedPreferences();
		
		// Instantiation
		EditTextPreference etp_name = (EditTextPreference) findPreference("preference_name");
		Preference p_username = findPreference("preference_username");
		Preference p_tally_single = findPreference("preference_singletally");
		Preference p_tally_trip = findPreference("preference_triptally");
		
		// Read values
		String string_name = localSharedPreferences.getString("preference_name", "null");
		String string_username = localSharedPreferences.getString("preference_username", "null");
		int int_tally_single = localSharedPreferences.getInt("preference_singletally", 0);
		int int_tally_trip = localSharedPreferences.getInt("preference_triptally", 0);
		
		// Set Summaries
		etp_name.setSummary(capitalizeFirstLetter(string_name));
		p_username.setSummary(capitalizeFirstLetter(string_username));
		p_tally_single.setSummary(int_tally_single + "");
		p_tally_trip.setSummary(int_tally_trip + "");
	}
	
	private String capitalizeFirstLetter(String paramString) {
		StringBuilder sb = new StringBuilder(paramString);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
	
}