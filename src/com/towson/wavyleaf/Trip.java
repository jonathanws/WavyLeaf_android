package com.towson.wavyleaf;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
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
	
	protected TextView tripInterval, tripSelection, tally, tallyNumber, tvlat, tvlong, tvpicnotes,
		tvper, tvper_summary, tvcoor, tvarea, tvarea_summary;
	protected EditText notes, etarea;
	protected Button doneTrip, b1, b2, b3, b4, b5, b6;
	protected RadioGroup rg;
	protected Spinner sp;
	protected Location gpsLocation;
	protected LocationManager mLocationManager;
	NotificationManager nm;
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
		
		nm = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
		nm.cancel(Main.mUniqueId);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		tallyNumber.setText(sp.getInt(Settings.KEY_TRIPTALLY_CURRENT, 0) + "");
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
//		ib = (ImageButton) findViewById(R.id.report_imagebutton);
		
		// Listener for camera button
//		ib.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				takePicture();
//			}
//		});
		
		// Listener for EditText in Area Infested
		etarea.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etarea.getText().length() == 0) {
					tvarea_summary.setText("");
				}
				else if (etarea.getText().toString().contains("-")) {	//negative number sign
					etarea.getEditableText().clear();
					Toast.makeText(getApplicationContext(), "Negative values not allowed", Toast.LENGTH_SHORT).show();
				}
				else {
					tvarea_summary.setText(etarea.getText() + " " + sp.getSelectedItem().toString());
				}
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
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nm.cancel(Main.mUniqueId);
		determineTally();
		determineTimeIntervalTextViews();
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
				if (requestUpdatesFromProvider() == null) // If no GPS
					Toast.makeText(getApplicationContext(), "Cannot submit without GPS signal", Toast.LENGTH_SHORT).show();
				else {
					// If all fields are filled out, minus Notes
					if (verifyFields() == true) {
						createJSONObject();
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
				break;
			case R.id.bu_2:
				tvper_summary.setText("1-10%");
				break;
			case R.id.bu_3:
				tvper_summary.setText("10-25%");
				break;
			case R.id.bu_4:
				tvper_summary.setText("25-50%");
				break;
			case R.id.bu_5:
				tvper_summary.setText("50-75%");
				break;
			case R.id.bu_6:
				tvper_summary.setText("75-100%");
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
		gpsLocation = requestUpdatesFromProvider();
		if (gpsLocation == null)
			Toast.makeText(getApplicationContext(), "No GPS signal", Toast.LENGTH_SHORT).show();
	}
	
	private Location requestUpdatesFromProvider() {
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		return gpsLocation;
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
	
	/** Verify that required fields are filled
	 *  @return boolean stating if all fields are filled out **/
	private boolean verifyFields() {
		boolean result = false;
		
		if (isToggleSelected()) {
			if (isAreaSelected()) {
				if (hasCoordinates())
					result = true;
			}
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
	
	// See if user selected an area
	public boolean isAreaSelected() {
		boolean result = false;
		
		if (etarea.getText().toString().trim().length() > 0)
			result = true;
		else
			Toast.makeText(getApplicationContext(), "Select an area", Toast.LENGTH_SHORT).show();
		
		return result;
	}
	
	// See if user has coordinates
	public boolean hasCoordinates() {
		boolean result = false;
		
		if (!(gpsLocation == null))
			result = true;
		else
			Toast.makeText(getApplicationContext(), "Error determining position", Toast.LENGTH_SHORT).show();
		
		return result;
	}
	
	private String shortenAreaType() {
		String str = sp.getSelectedItem().toString();
		if (str.equals("Square Miles")) {
			str = "SM";
		} else if (str.equals("Square Acres")) {
			str = "SA";
		} else {
			str = "SF";
		}
		
		return str;
	}
	
	protected double getAreaText() {
		if (etarea.getText().toString().trim().equals("") || (etarea.getText().toString().trim().equals(null)))
			return -1;
		else
			return Double.parseDouble(etarea.getText().toString());
	}
	
	private JSONObject createJSONObject() {
		Time now = new Time();
		now.setToNow();
		
		JSONObject trip = new JSONObject();
		try {
			trip.put(UploadData.ARG_USER_ID, "1"); 	//spref.getString(Settings.KEY_USERNAME, "null"));
			trip.put(UploadData.ARG_PERCENT, getSelectedToggleButton());
			trip.put(UploadData.ARG_AREAVALUE, getAreaText());
			trip.put(UploadData.ARG_AREATYPE, shortenAreaType());
			trip.put(UploadData.ARG_LATITUDE, gpsLocation.getLatitude());
			trip.put(UploadData.ARG_LONGITUDE, gpsLocation.getLongitude());
			trip.put(UploadData.ARG_NOTES, notes.getText());
			trip.put(UploadData.ARG_DATE, now.year + "-" + (now.month + 1) + "-" + now.monthDay + " " + now.hour + ":" + now.minute + ":" + now.second);
			//bitmap would go here
			
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Data not saved, try again", Toast.LENGTH_SHORT).show();
		}
		return trip;
	}
	
//	protected void takePicture() {
//		startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), CAMERA_REQUEST);
//	}
	
	protected void determineTally() {
		
		// Read values from local storage
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String string_tripcurrent = sp.getInt(Settings.KEY_TRIPTALLY_CURRENT, 0) + "";
		
		// Span to set text color to green
		final ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.parseColor("#669900"));
		
		// Span to make text bold
		final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
		
		final SpannableStringBuilder sb = new SpannableStringBuilder(string_tripcurrent);
		sb.setSpan(fcsGreen, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		
		this.tallyNumber.setText(sb);
	}
	
	protected void determineTimeIntervalTextViews() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String string_tripInterval = sp.getString(Settings.TRIP_INTERVAL, "(Not Set)");
		
		final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
		final SpannableStringBuilder sb = new SpannableStringBuilder(string_tripInterval);
		
		sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		tripSelection.setText(sb);
	}

}


