package com.towson.wavyleaf;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;

public class Main extends SherlockActivity implements OnClickListener {
	
	private static final int ONSTART = 6;
	private static final int HELP = 0;
	protected static final String TRIP_ENABLED_KEY = "trip_enabled";
	protected static final String TRIP_INTERVAL = "trip_interval";
	private static final String FIRST_RUN = "first_run"; 
	protected static final int mUniqueId = 24885251; // Used for notifications
	protected Button bu_new, bu_trip;
	protected TextView tripInterval, tripSelection, tally, tallyNumber;
	NotificationManager nm;
	public CountDownTimer ctd;
	public AlarmManager am;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		//Bugsense integration
		BugSenseHandler.initAndStartSession(getApplicationContext(), "beb5fcad");
		
		setContentView(R.layout.layout_main);
		initLayout();
		determineButtonDrawable();
		checkForFirstRun();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int int_single = sp.getInt(Settings.KEY_SINGLETALLY, 0);
        int int_trip = sp.getInt(Settings.KEY_TRIPTALLY, 0);
        this.tallyNumber.setText(int_single + " / " + int_trip);
    }
	
	protected void initLayout() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		
		bu_new = (Button) findViewById(R.id.button_new);
		bu_trip = (Button) findViewById(R.id.button_trip);
		tripInterval = (TextView) findViewById(R.id.tv_tripinterval);
		tripSelection = (TextView) findViewById(R.id.tv_tripselection);
		tally = (TextView) findViewById(R.id.tv_triptally);
		tallyNumber = (TextView) findViewById(R.id.tv_triptallynumber);
		
		bu_new.setTypeface(tf_light);
		bu_trip.setTypeface(tf_light);
		tripInterval.setTypeface(tf_light);
		tripSelection.setTypeface(tf_light);
		tally.setTypeface(tf_light);
		tallyNumber.setTypeface(tf_light);
		
		bu_new.setOnClickListener(this);
		bu_trip.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(mUniqueId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//BugSenseHandler.closeSession(Main.this);
		nm.cancel(mUniqueId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Deprecated
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				Intent settingsIntent = new Intent(this, Settings.class);
				this.startActivity(settingsIntent);
				return true;
			case R.id.menu_help:
				showDialog(HELP);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Deprecated
	@Override
	public void onClick(View view) {
		if (view == this.bu_new) {
			Intent newReportIntent = new Intent(this, Report.class);
			this.startActivity(newReportIntent);	
			
		} else if (view == this.bu_trip) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			if (sp.getBoolean(TRIP_ENABLED_KEY, false) == false) { // if user wants to start a new trip
				showDialog(ONSTART);
				startService(new Intent(Main.this, ReminderService.class));
			}
			else if (sp.getBoolean(TRIP_ENABLED_KEY, false) == true) { // If trip already in session
				sp.edit().putBoolean(TRIP_ENABLED_KEY, false).commit(); // Turn it off
				tripSelection.setText("- - -");
				determineButtonDrawable();
				
				stopService(new Intent(Main.this, ReminderService.class));
				
				Intent alarmIntent = new Intent(this, AlarmReceiver.class);
				PendingIntent sender = PendingIntent.getBroadcast(this, 1, alarmIntent, 0);
				
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				
				if (ctd != null)
					ctd.cancel();
			}
		}
	}
	
	@Deprecated
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
				
			case ONSTART:
				return new AlertDialog.Builder(this)
				.setTitle("Choose Reminder Interval")
				.setItems(R.array.tripinterval_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						if (which == 0) {
							intervalSelected("5:00", "Five Minutes", 3000); // TODO change back to 300000
						}
						else if(which == 1) {
							intervalSelected("10:00", "Ten Minutes", 600000);
						}
						else if(which == 2) {
							intervalSelected("15:00", "Fifteen Minutes", 900000);
						}
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void determineButtonDrawable() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		if (sp.getBoolean(TRIP_ENABLED_KEY, false) == true) { // There is a trip in progress
        	setButtonDrawable(R.drawable.ic_main_end); // Set to red button
        	bu_trip.setText(R.string.layout_main_endtrip);	// Set text to "End Trip"
        } 
        else {
        	setButtonDrawable(R.drawable.ic_main_start_light); // Set to blue button
        	bu_trip.setText(R.string.layout_main_trip);	// Set text back to "Start Trip"
        }
	}
	
	protected void intervalSelected(String timeNum, String timeString, int milli) {
		Log.i("wavy", "intervalselected");
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		ed.putString(TRIP_INTERVAL, timeNum);
		ed.putBoolean(TRIP_ENABLED_KEY, true);
	    ed.commit();
	    
	    setEditText(this.tripSelection, timeNum);
	    determineButtonDrawable();
	    Toast.makeText(getApplicationContext(), timeString, Toast.LENGTH_SHORT).show();
	    startTimer(milli);
	    
	    // Set alarm intent
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 1, alarmIntent, 0);
		
		// Create repeating alarm
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milli, milli, sender);
	}
	
	protected void setButtonDrawable(int button) {
		Drawable img = getBaseContext().getResources().getDrawable(button);
		bu_trip.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
	}
	
	protected void setButtonText(Button button, String text) {
		button.setText(text);
	}
	
	protected void setEditText(TextView tv, String message) {
		tv.setText(message);
	}
	
	protected void startTimer(int countDownFrom) {
		ctd = new CountDownTimer(countDownFrom, 1000) {
			public void onTick(long millisUntilFinished) {
				tripSelection.setText(String.format("%d:%02d",
						((millisUntilFinished / 1000) / 60),
						((millisUntilFinished / 1000) % 60)));
			}
			public void onFinish() {
				tripSelection.setText("- - -");
			}
		}.start();
	}
	
	protected void checkForFirstRun() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if (sp.getBoolean(FIRST_RUN, true)) {
			sp.edit().putBoolean(FIRST_RUN, false).commit();
			Intent newReportIntent = new Intent(this, Login.class);
			this.startActivity(newReportIntent);
		}
	}
	
	// Dev use
	public void testPHP(View view) {
		
		JSONObject json = new JSONObject();
		try {
			json.put("name", "my awesome name");
			json.put("birthyear", "1234");
			json.put("education", "college");
			json.put("outdoorexperience", "none");
			json.put("generalplantid", "clueless");
			json.put("wavyleafid", "clueless");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		new UploadData(this, UploadData.TASK_SUBMIT_USER).execute(json);
	}

}




