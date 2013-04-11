package com.towson.wavyleaf;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockActivity implements OnClickListener {
	
	private static final int ONSTART = 6;
	private static final int HELP = 0;
	private static final String TRIP_ENABLED_KEY = "trip_enabled";
	public boolean tripEnabled = false;
	protected Button bu_new, bu_trip;
	protected TextView tripInterval, tripSelection, tally, tallyNumber;
	private AlarmManager intervalAlarm;
	private Intent alarmIntent;
	private PendingIntent pendingAlarm;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_main);
		initLayout();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        tripEnabled = sp.getBoolean(TRIP_ENABLED_KEY, false);
        
        if (tripEnabled) {
        	setButtonDrawable(R.drawable.ic_main_end);
        	//setup alarm for trips
        	intervalAlarm = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmIntent = new Intent(getApplicationContext(), Trip.class);
			pendingAlarm = PendingIntent.getBroadcast(getApplicationContext(), 117, alarmIntent, 0);
			
        } 
        else if (!tripEnabled) {
        	setButtonDrawable(R.drawable.ic_main_start_light);
        	if(intervalAlarm != null)
        	{
        		intervalAlarm.cancel(pendingAlarm);
        	}
        }
        
        Toast.makeText(getApplicationContext(), tripEnabled + "", Toast.LENGTH_SHORT).show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_help:
				showDialog(HELP);
				return true;
			// Delete this!
			case R.id.deleteme:
				Intent sessionIntent = new Intent(this, Trip.class);
				this.startActivity(sessionIntent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View view) {
		if (view == this.bu_new) {
			Intent newReportIntent = new Intent(this, Report.class);
			this.startActivity(newReportIntent);	
			
		} else if (view == this.bu_trip) {	        
			// Toggle boolean
			tripEnabled = !tripEnabled;
			
			// Commit change
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			Editor ed = sp.edit();
			ed.putBoolean(TRIP_ENABLED_KEY, tripEnabled);
			ed.commit();
			Toast.makeText(getApplicationContext(), tripEnabled + "", Toast.LENGTH_SHORT).show();
			
			// Set drawable
			if(tripEnabled) {
				setButtonDrawable(R.drawable.ic_main_end);
				showDialog(ONSTART);
			} else if (!tripEnabled)
				setButtonDrawable(R.drawable.ic_main_start_light);
				
//			Intent sessionIntent = new Intent(this, Trip.class);
//			this.startActivity(sessionIntent);
		}
	}
	
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
				.setTitle("Choose Interval")
				.setItems(R.array.tripinterval_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						Editor ed = sp.edit();						
						
						if (which == 0) {
							intervalAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 300000, pendingAlarm);
							ed.putString("TRIP_INTERVAL", "5:00 Minutes");
							Toast.makeText(getApplicationContext(), "Five Minutes", Toast.LENGTH_SHORT).show();
						}
						else if(which == 1) {
							intervalAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 600000, pendingAlarm);
							ed.putString("TRIP_INTERVAL", "10:00 Minutes");
							Toast.makeText(getApplicationContext(), "Ten Minutes", Toast.LENGTH_SHORT).show();
						}
						else if(which == 2) {
							intervalAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 900000, pendingAlarm);
							ed.putString("TRIP_INTERVAL", "15:00 Minutes");
							Toast.makeText(getApplicationContext(), "Fifteen Minutes", Toast.LENGTH_SHORT).show();
						}
						else if(which == 3) {
							intervalAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1200000, pendingAlarm);
							ed.putString("TRIP_INTERVAL", "20:00 Minutes");
							Toast.makeText(getApplicationContext(), "Twenty Minutes", Toast.LENGTH_SHORT).show();
						}
						else if(which == 4) {
							intervalAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800000, pendingAlarm);
							ed.putString("TRIP_INTERVAL", "30:00 Minutes");
							Toast.makeText(getApplicationContext(), "Thirty Minutes", Toast.LENGTH_SHORT).show();
						}
						
						ed.commit();
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void setButtonDrawable(int button) {
		Drawable img = getBaseContext().getResources().getDrawable(button);
		bu_trip.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
	}

}
