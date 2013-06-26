package com.towson.wavyleaf;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Trip extends SherlockActivity {
	
	private boolean gpsEnabled = false;
	protected Button doneTrip, b1, b2, b3, b4, b5, b6;
	protected EditText notes, etarea;
	protected Location currentEditableLocation;
	protected LocationManager mLocationManager;
	protected RadioGroup rg;
	protected Spinner sp;
	protected TextView tripInterval, tripSelection, tally, tallyNumber, tvlat, tvlong, tvpicnotes,
		tvper, tvper_summary, tvcoor, tvarea, tvarea_summary;
	NotificationManager nm;
	private LocationApplication locationData;	
	private Timer updateLocationTimer;
	

	private static final int ONE_MINUTE = 1000*60; //in ms
	private static final int FIVE_SECONDS = 1000*5; //in ms
	
	// private static final int CAMERA = 3;
	// private static final int GALLERY_REQUEST = 1339;
	// private static final int CAMERA_REQUEST = 1337;
	// protected ImageButton ib;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_trip);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		init();
		
		// Apparently useless code if this is called in onResume()?
		nm = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
		nm.cancel(Main.notifTripID);
	}
	
	private void init() {
		getWindow().setBackgroundDrawable(null);
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		Typeface tf_bold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
		
		tripInterval = (TextView) findViewById(R.id.tv_tripinterval);
		tripSelection = (TextView) findViewById(R.id.tv_tripselection);
		tally = (TextView) findViewById(R.id.tv_triptally);
		tallyNumber = (TextView) findViewById(R.id.tv_triptallynumber);
		
		tvlat = (TextView) findViewById(R.id.tv_latitude);
		tvlong = (TextView) findViewById(R.id.tv_longitude);
		tvpicnotes = (TextView) findViewById(R.id.tv_picturenotes);
		tvper = (TextView) findViewById(R.id.tv_percentageseen);
		tvper_summary = (TextView) findViewById(R.id.tv_percentageseen_summary);
		tvcoor = (TextView) findViewById(R.id.tv_coordinates);
		tvarea = (TextView) findViewById(R.id.tv_areainfested);
		tvarea_summary = (TextView) findViewById(R.id.tv_areainfested_summary);
		notes = (EditText) findViewById(R.id.notes);
		etarea = (EditText) findViewById(R.id.et_areainfested);
		b1 = (ToggleButton) findViewById(R.id.bu_1);
		b2 = (ToggleButton) findViewById(R.id.bu_2);
		b3 = (ToggleButton) findViewById(R.id.bu_3);
		b4 = (ToggleButton) findViewById(R.id.bu_4);
		b5 = (ToggleButton) findViewById(R.id.bu_5);
		b6 = (ToggleButton) findViewById(R.id.bu_6);
		rg = (RadioGroup) findViewById(R.id.toggleGroup);
		sp = (Spinner) findViewById(R.id.sp_areainfested);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		
		locationData = (LocationApplication) getApplication();
		currentEditableLocation = locationData.getLocation();
		
		updateLocationTimer = new Timer();
		TimerTask updateLocationTask = new TimerTask(){			
			@Override
			public void run() {
				checkLocation();
			}
			
		};
		updateLocationTimer.scheduleAtFixedRate(updateLocationTask , 0, FIVE_SECONDS);
		
		
		// Listener for EditText in Area Infested
		etarea.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etarea.getText().length() == 0)
					tvarea_summary.setText("");
				else if (etarea.getText().toString().contains("-")) {	// negative number sign
					etarea.getEditableText().clear();
					Toast.makeText(getApplicationContext(), "Use a positive value", Toast.LENGTH_SHORT).show();
				} else
					tvarea_summary.setText(etarea.getText() + " " + sp.getSelectedItem().toString());
			}
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});
		
		// Listener for Spinner in Area Infested
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (etarea.getText().length() != 0)
					tvarea_summary.setText(etarea.getText() + " " + sp.getSelectedItem());
			}
		});
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.areainfested_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		
		//set all the beautiful typefaces
		tripInterval.setTypeface(tf_light);
		tripSelection.setTypeface(tf_light);
		tally.setTypeface(tf_light);
		tallyNumber.setTypeface(tf_light);
		tvlat.setTypeface(tf_light);
		tvlong.setTypeface(tf_light);
		tvcoor.setTypeface(tf_bold);
		tvarea.setTypeface(tf_bold);
		tvarea_summary.setTypeface(tf_bold);
		tvper.setTypeface(tf_bold);
		tvper_summary.setTypeface(tf_bold);
		tvpicnotes.setTypeface(tf_bold);
		b1.setTypeface(tf_light);
		b2.setTypeface(tf_light);
		b3.setTypeface(tf_light);
		b4.setTypeface(tf_light);
		b5.setTypeface(tf_light);
		b6.setTypeface(tf_light);
		
//		ib = (ImageButton) findViewById(R.id.report_imagebutton);
		// Listener for camera button
//		ib.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                takePicture();
//            }
//        });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (nm != null)
			nm.cancel(Main.notifTripID);
		
		determineTally();
		determineTimeIntervalTextViews();
		
		// Check for GPS
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		locationData = (LocationApplication) getApplication();
		currentEditableLocation = locationData.getLocation();
		
		updateLocationTimer = new Timer();
		TimerTask updateLocationTask = new TimerTask(){			
			@Override
			public void run() {
				checkLocation();
			}
			
		};
		updateLocationTimer.scheduleAtFixedRate(updateLocationTask , 0, FIVE_SECONDS);
		
		wheresWaldo();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getSupportMenuInflater().inflate(R.menu.menu_trip, menu);
    	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent mainIntent = new Intent(this, Main.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mainIntent);
				finish();
				return true;
			case R.id.menu_submit:

//	        	Toast.makeText(getApplicationContext(), String.valueOf(currentEditableLocation.getTime()), Toast.LENGTH_SHORT).show();
	    		if (isAccurateLocation(currentEditableLocation)){//if LocationListener is accurate
	            	// If all fields are filled out, minus Notes/Area infested
	    			if (verifyFields() == true) {
	    				Toast.makeText(getApplicationContext(), "Sighting recorded", Toast.LENGTH_SHORT).show();
	            		createJSONObject();
	            		finish();
	            	}
	    		} else if (requestUpdatesFromProvider() == null) // If no GPS
	    			Toast.makeText(getApplicationContext(), "Cannot submit without GPS signal", Toast.LENGTH_SHORT).show();
	    		else {
	            	// If all fields are filled out, minus Notes/Area infested
	    			if (verifyFields() == true) {
	    				Toast.makeText(getApplicationContext(), "Sighting recorded", Toast.LENGTH_SHORT).show();
	            		createJSONObject();
	            		// Restore preferences
	            		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	            	    boolean tripEnabled = sp.getBoolean("TRIP_ENABLED",false);
	            	    if(!tripEnabled){
	            			locationData.stop();	
	            	    }
	            		updateLocationTimer.cancel();
	            		finish();
	            	}
	    		}
	    		return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Method only allows one ToggleButton to be set to true at a time
	// Call to this method is defined in xml for each togglebutton
	public void onToggle(View view) {
		//loop through all children in radiogroup.  In this case, two lin layouts
		for (int i = 0; i < rg.getChildCount(); i++) {
			View child = rg.getChildAt(i);
			//if child is lin layout (we already know all children are lin layouts)
			if (child instanceof LinearLayout) {
				//then loop through three toggles
				for (int j = 0; j < ((ViewGroup)child).getChildCount(); j++) {
					final ToggleButton tog = (ToggleButton) ((ViewGroup)child).getChildAt(j);
					//have only one togglebutton selected at one time
					if (tog != view)
						tog.setChecked(false);
				}
			}
		}
		
		// Determine text to set to textview
		switch (view.getId()) {
			case R.id.bu_1:
				tvper_summary.setText("0%");
				etarea.setText("0");			// If user says they don't see any, then area infested is obviously zero
				break;
			case R.id.bu_2:
				tvper_summary.setText("1-10%");
				etarea.setText("");
				break;
			case R.id.bu_3:
				tvper_summary.setText("10-25%");
				etarea.setText("");
				break;
			case R.id.bu_4:
				tvper_summary.setText("25-50%");
				etarea.setText("");
				break;
			case R.id.bu_5:
				tvper_summary.setText("50-75%");
				etarea.setText("");
				break;
			case R.id.bu_6:
				tvper_summary.setText("75-100%");
				etarea.setText("");
				break;
			default:
				tvper_summary.setText("");
		}
	}
	
	// Get value of toggle selected
	public String getSelectedToggleButton() {
		String str = "";
		for (int i = 0; i < rg.getChildCount(); i++) {
			View child = rg.getChildAt(i);
			if (child instanceof LinearLayout) {
				for (int j = 0; j < ((ViewGroup)child).getChildCount(); j++) {
					final ToggleButton tog = (ToggleButton) ((ViewGroup)child).getChildAt(j);
					if (tog.isChecked())
						str = tog.getText().toString();
				}
			}
		}
		return str;
	}
	
	// Method won't be called unless Play APK in installed
	public void wheresWaldo() {
		currentEditableLocation = requestUpdatesFromProvider();
		
		if (!(currentEditableLocation == null))
			setEditTexts(currentEditableLocation.getLatitude(), currentEditableLocation.getLongitude());
		
		else if (currentEditableLocation == null)
			Toast.makeText(getApplicationContext(), "No GPS signal", Toast.LENGTH_SHORT).show();
	}
	
	private Location requestUpdatesFromProvider() {
		currentEditableLocation = locationData.getLocation();
		
        if(isAccurateLocation(currentEditableLocation))
        	return currentEditableLocation;
		
		Location location = null;
		return location;
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		if ((requestCode == CAMERA_REQUEST) && (resultCode == RESULT_OK)) {
//			Bitmap bm = (Bitmap) data.getExtras().get("data");
//			ib.setImageBitmap(bm);
//		}
//	}
	
	protected void setEditTexts(double latitude, double longitude) {
		tvlat.setText("Latitude:\t\t\t" + latitude);
		tvlong.setText("Longitude:\t\t" + longitude);
	}
	
	/** Verify that required fields are filled
	 *  @return boolean stating if all fields are filled out **/
	private boolean verifyFields() {
		boolean result = false;
		
		if (isToggleSelected()) {
			if (hasCoordinates())
				result = true;
		}
		
		return result;
	}
	
	// See if any toggle buttons are selected
	// Not sure if this method is required when there are two others like it... but it's 3am. So yes it is.
	public boolean isToggleSelected() {
		boolean result = false;
		
		for (int i = 0; i < rg.getChildCount(); i++) {
			View child = rg.getChildAt(i);
			if (child instanceof LinearLayout) {
				for (int j = 0; j < ((ViewGroup)child).getChildCount(); j++) {
					final ToggleButton tog = (ToggleButton) ((ViewGroup)child).getChildAt(j);
					if (tog.isChecked())
						result = true;
				}
			}
		}
		
		if (result == false)
			Toast.makeText(getApplicationContext(), "Select a percentage", Toast.LENGTH_SHORT).show();
			
		return result;
	}
	
	// See if user has coordinates
	public boolean hasCoordinates() {
		boolean result = false;
		
		if (!(currentEditableLocation == null))
			result = true;
		else
			Toast.makeText(getApplicationContext(), "Error determining position", Toast.LENGTH_SHORT).show();
		
		return result;
	}
	
	private String shortenAreaType() {
		String str = sp.getSelectedItem().toString();
		if (str.equals("Hectares"))
			str = "HA";
		else if (str.equals("Square Metres"))
			str = "SM";
		else if (str.equals("Acres"))
			str = "SA";
		else
			str = "SF";
		
		return str;
	}
	
	protected double getAreaText() {
		if (etarea.getText().toString().trim().equals("") || (etarea.getText().toString().trim().equals(null)))
			return -1;
		else
			return Double.parseDouble(etarea.getText().toString());
	}
	
	private void createJSONObject() {
		Time now = new Time();
		now.setToNow();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		JSONObject trip = new JSONObject();
		
		try {
			trip.put(UploadData.ARG_USER_ID, sp.getString(Settings.KEY_USER_ID, "null"));
			trip.put(UploadData.ARG_PERCENT, getSelectedToggleButton());
			trip.put(UploadData.ARG_AREAVALUE, getAreaText());
			trip.put(UploadData.ARG_AREATYPE, shortenAreaType());
			trip.put(UploadData.ARG_LATITUDE, currentEditableLocation.getLatitude());
			trip.put(UploadData.ARG_LONGITUDE, currentEditableLocation.getLongitude());
			trip.put(UploadData.ARG_NOTES, notes.getText());
			trip.put(UploadData.ARG_DATE, now.year + "-" + (now.month + 1) + "-" + now.monthDay + " " + now.hour + ":" + now.minute + ":" + now.second);
			//bitmap would go here
			
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Data not saved, try again", Toast.LENGTH_SHORT).show();
		}
		
		new UploadData(this, UploadData.TASK_SUBMIT_POINT).execute(trip);
	}
	
	protected void determineTally() {		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		tallyNumber.setText(sp.getInt(Settings.KEY_TRIPTALLY, 0) + "");
	}
	
	protected void determineTimeIntervalTextViews() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String string_tripInterval = sp.getString(Settings.TRIP_INTERVAL, "(Not Set)");
		
		final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
		final SpannableStringBuilder sb = new SpannableStringBuilder(string_tripInterval);
		
		sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		tripSelection.setText(sb);
	}
	
//	protected void takePicture() {
//		startActivityforResult(new Intent("android.media.action.IMAGE_CAPTURE"), CAMERA_REQUEST);
//	}
	
	private void checkLocation()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_UI_Thread);
	}


	private Runnable Timer_UI_Thread = new Runnable() {
		public void run() {
		
		//This method runs in the same thread as the UI.    	       
		locationData = (LocationApplication) getApplication();
		currentEditableLocation = locationData.getLocation();
		
		if (currentEditableLocation != null){
			updateUILocation(currentEditableLocation);
		}else
			wheresWaldo();
	
		}
	};
	
	private void findUsersLocation(){
		locationData = (LocationApplication) getApplication();
		locationData.init();
	}
	
	private void updateUILocation(Location location) {
		setEditTexts(location.getLatitude(), location.getLongitude());
	}
	
private boolean isAccurateLocation(Location location){
		
		if(location == null)
			return false;
		
		boolean isRecent = (location.getTime() + ONE_MINUTE) > System.currentTimeMillis();
		
		//Toast.makeText(this, String.valueOf(location.getTime()) + " current: " + String.valueOf(System.currentTimeMillis()), Toast.LENGTH_SHORT).show();
		
		if(isRecent){
			//if recent, it is accurate
			return true;
		}
		
		return false;
		
	}

}


