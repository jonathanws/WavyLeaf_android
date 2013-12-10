package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class Settings extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String PREFERENCE = "preference_";
	
	public static final String KEY_BIRTHYEAR = "preference_birthyear";
	public static final String KEY_CHECKBOX_NOISE = "preference_noise";
	public static final String KEY_CHECKBOX_VIBRATE = "preference_vibrate";
	public static final String KEY_EMAIL = "preference_email";
	public static final String KEY_NAME = "preference_name";
	public static final String KEY_SINGLETALLY = "preference_singletally";
	public static final String KEY_SPLASH = PREFERENCE + "splash";
	public static final String KEY_THEME =  PREFERENCE + "theme";
	public static final String KEY_TRIPTALLY = "preference_triptally";
	public static final String KEY_TRIPTALLY_CURRENT = "preference_triptally_current"; // Key for tally for only current trip
	public static final String KEY_USER_ID = "preference_user_id";
	
	public static final String TRIP_ENABLED_KEY = "trip_enabled";
	public static final String TRIP_INTERVAL = "trip_interval";
	public static final String TRIP_INTERVAL_MILLI="trip_interval_milli";
	public static final String FIRST_RUN = "first_run"; 
	public static final String CURRENT_COUNTDOWN_SECOND = "current_countdown";
	
	public static final int DARK_THEME = R.style.Theme_Sherlock;
	public static final int LIGHT_THEME = R.style.Theme_Sherlock_Light;
	public static int current_theme = DARK_THEME;

	@Deprecated
	@Override
	protected void onCreate(Bundle paramBundle) {
		setTheme(current_theme);
		super.onCreate(paramBundle);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		setSummaries();
		
		// Implement a long click listener for select elements
		// http://kmansoft.com/2011/08/29/implementing-long-clickable-preferences/
		ListView listView = getListView();
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                ListAdapter listAdapter = listView.getAdapter();
                Object obj = listAdapter.getItem(position);
                
                if (obj != null && obj instanceof View.OnLongClickListener) {
                    View.OnLongClickListener longListener = (View.OnLongClickListener) obj;
                    return longListener.onLongClick(view);
                }
                return false;
            }
        });
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
		Preference p_tally_single = findPreference(KEY_SINGLETALLY);
		Preference p_tally_trip = findPreference(KEY_TRIPTALLY);
		CheckBoxPreference cbp_vibrate = (CheckBoxPreference) findPreference(KEY_CHECKBOX_VIBRATE);
		CheckBoxPreference cbp_noise = (CheckBoxPreference) findPreference(KEY_CHECKBOX_NOISE);
		CheckBoxPreference cbp_splash = (CheckBoxPreference) findPreference(KEY_SPLASH);
		
		// Read values
		String string_age = sp.getString(KEY_BIRTHYEAR, "null");
		String string_name = sp.getString(KEY_NAME, "null");
		int int_tally_single = sp.getInt(KEY_SINGLETALLY, 0);
		int int_tally_trip = sp.getInt(KEY_TRIPTALLY, 0);
		boolean boolean_vibrate = sp.getBoolean(KEY_CHECKBOX_VIBRATE, true);
		boolean boolean_noise = sp.getBoolean(KEY_CHECKBOX_NOISE, true);
		boolean boolean_splash = sp.getBoolean(KEY_SPLASH, true);
		
		// Set Summaries
		p_name.setSummary(capitalizeFirstLetter(string_name));
		p_age.setSummary(capitalizeFirstLetter(string_age));
		p_tally_single.setSummary(int_tally_single + "");
		p_tally_trip.setSummary(int_tally_trip + "");
		cbp_vibrate.setChecked(boolean_vibrate);
		cbp_noise.setChecked(boolean_noise);
		cbp_splash.setChecked(boolean_splash);
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
	
	// http://stackoverflow.com/questions/11251901/check-whether-database-is-empty
//	protected boolean isDBEmpty() {
//		DatabaseListJSONData m_dbListData = new DatabaseListJSONData(this);
//		SQLiteDatabase db = m_dbListData.getWritableDatabase();
//		
//		Cursor cur = db.rawQuery("SELECT * FROM " + DatabaseConstants.TABLE_NAME, null);
//		if (cur.moveToFirst())
//			return false;
//		else
//			return true;
//	}
	
}