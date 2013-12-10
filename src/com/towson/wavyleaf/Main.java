package com.towson.wavyleaf;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;

public class Main extends SherlockActivity implements OnClickListener {
	
	private static final int ONSTART = 6;
	protected static final int notifReminderID = 24885250, notifTripID = 24885251; // Used for notifications
	protected Button bu_new, bu_trip;//, bu_upload;
	protected TextView tripInterval, tripSelection;//, tally, tallyNumber;
	NotificationManager nm;
//	public CountDownTimer ctd;
	public AlarmManager am;
	public LocationApplication locationData;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		// Bugsense integration
		BugSenseHandler.initAndStartSession(getApplicationContext(), "beb5fcad");
		
		setContentView(R.layout.layout_main);
		initLayout();
		determineButtonDrawable();
		toggleFirstRun();
		
		if (!isDBEmpty()) {
			Intent uploadIntent = new Intent(this, UploadActivity.class);
			startActivity(uploadIntent);
		}
    }
	
	
	protected void initLayout() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		
		bu_new = (Button) findViewById(R.id.button_new);
		bu_trip = (Button) findViewById(R.id.button_trip);
//		bu_upload = (Button) findViewById(R.id.button_uploadsightings);
		tripInterval = (TextView) findViewById(R.id.tv_tripinterval);
		tripSelection = (TextView) findViewById(R.id.tv_tripselection);
		
		bu_new.setTypeface(tf_light);
		bu_trip.setTypeface(tf_light);
//		bu_upload.setTypeface(tf_light);
		tripInterval.setTypeface(tf_light);
		tripSelection.setTypeface(tf_light);
		
		bu_new.setOnClickListener(this);
		bu_trip.setOnClickListener(this);
//		bu_upload.setOnClickListener(this);
		
//		bu_upload.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		determineTallys();
		determineButtonDrawable();
//		if (ctd != null) {
//			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//			restartTimer(sp.getInt(Settings.CURRENT_COUNTDOWN_SECOND, 0));
//		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BugSenseHandler.closeSession(Main.this);
		if (nm != null) {
			nm.cancel(notifReminderID);
			nm.cancel(notifTripID);
		}
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
			
			case R.id.menu_help:
				Intent helpIntent = new Intent(this, HelpExpanded.class);
				this.startActivity(helpIntent);
				return true;
				
			case R.id.menu_settings:
				// This would be moved to before the super() call in the Settings() class, but
				// apparently you cannot make a SharedPreferences call before the super() call.
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				if (sp.getBoolean(Settings.KEY_THEME, true))
					Settings.current_theme = Settings.DARK_THEME;
				else
					Settings.current_theme = Settings.LIGHT_THEME;
				
				Intent settingsIntent = new Intent(this, Settings.class);
				this.startActivity(settingsIntent);
				return true;
				
			case R.id.menu_feedback:
				Intent intent = new Intent();
				intent = assembleEmail();
				startActivity(Intent.createChooser(intent, "Send mail"));
				return true;
				
			case R.id.menu_about:
				Intent aboutIntent = new Intent(this, About.class);
				this.startActivity(aboutIntent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Deprecated
	@Override
	public void onClick(View view) {
		if (view == this.bu_new) {
			//start searching for location
			findUsersLocation();
			
			Intent newSightingIntent = new Intent(this, Sighting.class);
			this.startActivity(newSightingIntent);	
			
		} else if (view == this.bu_trip) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			if (sp.getBoolean(Settings.TRIP_ENABLED_KEY, false) == false) { // if user wants to start a new trip
				showDialog(ONSTART);
				
				//start searching for location
				findUsersLocation();
				
				// 6 hour reminder service
				startService(new Intent(Main.this, ReminderService.class));
			}
			else if (sp.getBoolean(Settings.TRIP_ENABLED_KEY, false) == true) { // If trip already in session
				sp.edit().putBoolean(Settings.TRIP_ENABLED_KEY, false).commit(); // Turn it off
				tripSelection.setText("- - -");
				determineButtonDrawable();
				
				//stop searching for location
				stopSearchingForLocation();
				
				// Cancel any existing trip notifications in system bar, since trip is now finished
				nm = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
				nm.cancel(Main.notifTripID);
				
				//TODO I really don't think this code should be here
				// 6 hour reminder service
//				stopService(new Intent(Main.this, ReminderService.class));
				
				// Trip notification service
				Intent alarmIntent = new Intent(this, AlarmReceiver.class);
				PendingIntent sender = PendingIntent.getBroadcast(this, 1, alarmIntent, 0);
				
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				
//				if (ctd != null)
//					ctd.cancel();
			}
		} //else if (view == this.bu_upload) {
//			Intent pushIntent = new Intent(this, UploadActivity.class);
//			this.startActivity(pushIntent);	
//		}
	}
	
	@Deprecated
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		
//			case HELP:
//				return new AlertDialog.Builder(this)
//				.setTitle("External Link")
//				.setMessage("Help documents can be found online.\n\nContinue?")
//				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						// My name is Android. I don't allow pasted code that works somewhere else. Derp.
//						goToHelp();
//					}
//				})
//				.setNegativeButton("cancel", null)
//				.create();
				
			case ONSTART:
				return new AlertDialog.Builder(this)
				.setTitle("Choose Reminder Interval")
				.setItems(R.array.tripinterval_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						if (which == 0) {
							intervalSelected("5:00", "Five Minutes", 300000);
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
	
	// http://stackoverflow.com/questions/11251901/check-whether-database-is-empty
	protected boolean isDBEmpty() {
		DatabaseListJSONData m_dbListData = new DatabaseListJSONData(this);
		SQLiteDatabase db = m_dbListData.getWritableDatabase();
		
		Cursor cur = db.rawQuery("SELECT * FROM " + DatabaseConstants.TABLE_NAME, null);
		if (cur.moveToFirst()) {
			db.close();
			return false;
		} else {
			db.close();
			return true;
		}
	}
	
	// Determine state for Trip button, and for Upload sightings button
	protected void determineButtonDrawable() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		// Trip button
		if (sp.getBoolean(Settings.TRIP_ENABLED_KEY, false) == true) { // There is a trip in progress
        	setButtonDrawable(bu_trip, R.drawable.ic_main_end); // Set to red button
        	bu_trip.setText(R.string.layout_main_endtrip);	// Set text to "End Trip"
        } 
        else {
        	setButtonDrawable(bu_trip, R.drawable.ic_main_start_light); // Set to blue button
        	bu_trip.setText(R.string.layout_main_trip);	// Set text back to "Start Trip"
        }
		
		//TODO fix upload button... kinda
//		// Upload sightings button
//		if (isDBEmpty()) {
//			bu_upload.setClickable(false);
//			bu_upload.setTextColor(getResources().getColor(R.color.grey));
//			setButtonDrawable(bu_upload, R.drawable.ic_main_upload_light);
//		}
//		// TODO
//		//
//		// ***I'm not sure if this will be taken care of by xml, or if we really need this***
//		else {
//			bu_upload.setClickable(true);
//			bu_upload.setTextColor(getResources().getColor(R.color.black));
//			setButtonDrawable(bu_upload, R.drawable.ic_main_upload);
//		}
//		bu_upload.setVisibility(View.INVISIBLE);
	}
	
	protected void intervalSelected(String timeNum, String timeString, int milli) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor ed = sp.edit();
		ed.putString(Settings.TRIP_INTERVAL, timeNum);
		ed.putBoolean(Settings.TRIP_ENABLED_KEY, true);
		ed.putInt(Settings.TRIP_INTERVAL_MILLI, milli);
	    ed.commit();
	    
	    setEditText(this.tripSelection, timeNum);
	    determineButtonDrawable();
//	    startTimer(milli);
	    
	    // Set alarm intent
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 1, alarmIntent, 0);
		
		// Create repeating alarm
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + milli, milli, sender);
	}
	
	protected void setButtonDrawable(Button button, int newImage) {
		Drawable img = getBaseContext().getResources().getDrawable(newImage);
		button.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
	}
	
	protected void setButtonText(Button button, String text) {
		button.setText(text);
	}
	
	protected void setEditText(TextView tv, String message) {
		tv.setText(message);
	}
	
//	protected void restartTimer(int num) {
//		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//		CountDownTimer c = new CountDownTimer(num, 1000) {
//			public void onTick (long millsLeft) {
//				sp.edit().putInt(Settings.CURRENT_COUNTDOWN_SECOND, (int) millsLeft / 1000);
//				tripSelection.setText(String.format("%d:%02d",
//						((millsLeft / 1000) / 60),
//						((millsLeft / 1000) % 60)));
//			}
//			public void onFinish() {
//				if (sp.getBoolean(Settings.TRIP_ENABLED_KEY, false) == true)
//					startTimer(sp.getInt(Settings.TRIP_INTERVAL_MILLI, 0));
//			}
//		}.start();
//	}
//	
//	protected void startTimer(final int countDownFrom) {
//		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//		final int num = sp.getInt(Settings.TRIP_INTERVAL_MILLI, countDownFrom);
//		
//		ctd = new CountDownTimer(num, 1000) {
//			public void onTick(long millisUntilFinished) {
//				sp.edit().putInt(Settings.CURRENT_COUNTDOWN_SECOND, (int) millisUntilFinished / 1000);
//				tripSelection.setText(String.format("%d:%02d",
//						((millisUntilFinished / 1000) / 60),
//						((millisUntilFinished / 1000) % 60)));
//			}
//			public void onFinish() {
//				if (sp.getBoolean(Settings.TRIP_ENABLED_KEY, false) == true)
//					startTimer(num);
//				else {
//					sp.edit().putInt(Settings.CURRENT_COUNTDOWN_SECOND, 0).commit();
//					tripSelection.setText("- - -");
//				}
//			}
//		}.start();
//	}
	
	protected void toggleFirstRun() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if (sp.getBoolean(Settings.FIRST_RUN, true)) {
			sp.edit().putBoolean(Settings.FIRST_RUN, false).commit();
		}
	}
	
	protected void determineTallys() {
		
		// Read values from local storage
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int int_single = sp.getInt(Settings.KEY_SINGLETALLY, 0);
        int int_trip = sp.getInt(Settings.KEY_TRIPTALLY, 0);
        
        // Cast ints to strings. This is a funny hack
        String string_single = int_single + "";
        String string_trip = int_trip + "";
        
        // Span to set text color to our green / blue colors
        final ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.parseColor("#669900"));
        final ForegroundColorSpan fcsBlue = new ForegroundColorSpan(Color.parseColor("#0099CC"));
        final ForegroundColorSpan fcsBlack = new ForegroundColorSpan(Color.parseColor("#000000"));
        
        // Span to make text bold
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        
        final SpannableStringBuilder sb = new SpannableStringBuilder(string_single + " / " + string_trip);
        
        // Set the text color for each part
        sb.setSpan(fcsGreen, 0, sb.toString().indexOf("/") - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(fcsBlack, sb.toString().indexOf("/"), sb.toString().indexOf("/") + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(fcsBlue, sb.toString().indexOf("/") + 1, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        
        // make them also bold
        sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        
//        this.tallyNumber.setText(sb);
        
        // temporary
        if (sp.getBoolean(Settings.TRIP_ENABLED_KEY, false) == true)
        	this.tripSelection.setText(sp.getString(Settings.TRIP_INTERVAL, "0:00"));
        else
        	this.tripSelection.setText("- - -");
	}
	
	protected Intent assembleEmail() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = this.getResources();
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		
		String[] destination_email = res.getStringArray(R.array.email_array);
		String name = sp.getString(Settings.KEY_NAME, "null");
		String source_email = sp.getString(Settings.KEY_EMAIL, "null");
		String version = res.getString(R.string.version);
		
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, destination_email);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wavyleaf " + version);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				"Name:\t\t\t\t\t\t" + name + "\n" +
				"Email:\t\t\t\t\t\t\t" + source_email + "\n" +
				"Version:\t\t\t\t\t" + version + "\n" +
				"Device:\t\t\t\t\t\t" + getDeviceName() + "\n\n" +
//				"User Cool:\t\t\tenh, not sure" + "\n\n" +
				"- - - - - - - - - - -" + "\n\n");
		
		return emailIntent;
	}
	
	protected String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer))
			return capitalize(model);
		else
			return capitalize(manufacturer) + " " + model;
	}
	
	protected String capitalize(String s) {
		if (s == null || s.length() == 0)
			return "";
		else {
			StringBuilder sb = new StringBuilder(s);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			return sb.toString();
		}
	}
	
	private void findUsersLocation(){
		locationData = (LocationApplication) getApplication();
		locationData.init();
	}
	
	private void stopSearchingForLocation(){
		locationData = (LocationApplication) getApplication();
		locationData.stop();
	}
		
}


