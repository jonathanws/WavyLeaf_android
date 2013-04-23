package com.towson.wavyleaf;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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
	private static final String TRIP_INTERVAL = "trip_interval";
	protected static final int mUniqueId = 24885251;
	public boolean tripEnabled = false;
	protected Button bu_new, bu_trip;
	protected TextView tripInterval, tripSelection, tally, tallyNumber;
	private AlarmManager intervalAlarm;
	private Intent alarmIntent;
	private PendingIntent pendingAlarm;
	NotificationManager nm;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.layout_main);
		initLayout();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        tripEnabled = sp.getBoolean(TRIP_ENABLED_KEY, false);
        int int_single = sp.getInt(Settings.KEY_SINGLETALLY, 0);
        int int_trip = sp.getInt(Settings.KEY_TRIPTALLY, 0);
        this.tallyNumber.setText(int_single + " / " + int_trip);
        
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
        		intervalAlarm.cancel(pendingAlarm);
        }
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
			// Delete this!
			case R.id.deleteme:
				Intent sessionIntent = new Intent(this, Trip.class);
				this.startActivity(sessionIntent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Jon, this is the one you showed me via email -- MM
	@Deprecated
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
				.setTitle("Choose Interval")
				.setItems(R.array.tripinterval_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						if (which == 0) {
							intervalSelected("5:00 Minutes", "Five Minutes", 3000); // TODO change back to five minutes
						}
						else if(which == 1) {
							intervalSelected("10:00 Minutes", "Ten Minutes", 600000);
						}
						else if(which == 2) {
							intervalSelected("15:00 Minutes", "Fifteen Minutes", 900000);
						}
						else if(which == 3) {
							intervalSelected("20:00 Minutes", "Twenty Minutes", 1200000);
						}
						else if(which == 4) {
							intervalSelected("30:00 Minutes", "Thirty Minutes", 1800000);
						}
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void intervalSelected(String timeNum, String timeString, int milli) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		
		ed.putString(TRIP_INTERVAL, timeNum);
	    ed.commit();
	    setEditText(this.tripSelection, timeNum);
	    Toast.makeText(getApplicationContext(), timeString, Toast.LENGTH_SHORT).show();
	    startTimer(milli);
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
		new CountDownTimer(countDownFrom, 1000) {
			public void onTick(long millisUntilFinished) {
				tripSelection.setText( (int) ((millisUntilFinished / 1000) / 60) + ":" + (int) (millisUntilFinished / 1000) % 60);
			}
			public void onFinish() {
				tripSelection.setText("- - -");
				vibratePhone();
				createNotification();
			}
		}.start();
	}
	
	protected void vibratePhone() {
		int buzz = 150;
		int gap = 100;
		long[] pattern = {0, buzz, gap, buzz};
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(pattern, -1);
	}
	
	protected void createNotification() {
		Intent tripIntent = new Intent(this, Trip.class);
		tripIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //Resume activity instead of creating a new one
		PendingIntent pi = PendingIntent.getActivity(this, 0, tripIntent, 0);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setAutoCancel(true)
				.setContentIntent(pi)
				.setContentTitle("Timing interval elapsed")
				.setContentText("Record your next point.")
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("Hidden Text!")
				.setWhen(System.currentTimeMillis());
		
		nm.notify(mUniqueId, builder.build());
	}

}
