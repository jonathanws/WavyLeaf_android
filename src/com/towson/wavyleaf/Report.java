package com.towson.wavyleaf;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//TODO (later) obtain signature map key

/*
* This class requires the Google Play Services apk, and provides access to Google Maps v2.
* It is available on all phones with OpenGL v2.0 and Android 2.2+.  Requirements set in manifest
* This also uses abs
* 
* SETUP:  http://developer.android.com/google/play-services/setup.html
* Sdk Manager:  Extras >> Google Play Services
* 
* Sample Code is *then* located in sdk/extras/google/google_play_services/libproject/google-play-services_lib
*/

public class Report extends SherlockFragmentActivity {
	
	private static final int LEGAL = 1, CAMERA = 3, NO_GPS = 4; // Used for calling dialogs. arbitrary numbers
	private static final int CAMERA_REQUEST = 1337, EDIT_REQUEST = 1338, GALLERY_REQUEST = 1339;
	private boolean gpsEnabled = false;
	private boolean playAPKEnabled = false;
	private boolean editedCoordinatesInOtherActivitySoDontGetGPSLocation = false;
	private boolean mapHasMarker = false; // onResume keeps adding markers to map, this should stop it
	protected GoogleMap mMap;
	private UiSettings mUiSettings;
	protected ImageButton ib;
	protected RadioGroup rg;
	protected TextView tvlat, tvlong, tvpicnotes, tvper, tvper_summary, tvcoor, tvarea, tvarea_summary;
	protected EditText notes, etarea;
	protected ToggleButton b1, b2, b3, b4, b5, b6;
	protected LocationManager mLocationManager;
	protected CameraPosition userCurrentPosition;
	protected Spinner sp;
	protected Location currentEditableLocation; // Used by edit feature 
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_report);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		init();
		// Most setup methods are in onResume()
	}
	
	protected void init() {
		getWindow().setBackgroundDrawable(null);
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		Typeface tf_bold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
		
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
		ib = (ImageButton) findViewById(R.id.report_imagebutton);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Listener for camera button
		ib.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(CAMERA);
            }
        });
		
		// Listener for EditText in Area Infested
		etarea.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etarea.getText().length() == 0)
					tvarea_summary.setText("");
				else
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
		
		// User edited coordinates, so don't get them again from gps
		if (!editedCoordinatesInOtherActivitySoDontGetGPSLocation) {
			
			// Check for GPS
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			playAPKEnabled = doesDeviceHaveGooglePlayServices();
			
			// If GPS is disabled
			if (!gpsEnabled) {
				buildAlertMessageNoGps();
			} else if(gpsEnabled) {
				if (playAPKEnabled) {
					setUpMapIfNeeded();
					wheresWaldo();
				}
			}
		}
	}

	protected boolean doesDeviceHaveGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		//If user doesn't have the apk, then it prompts them to download it
		if (!(resultCode == ConnectionResult.SUCCESS)) {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
			return false;
		} else
			return true;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getSupportMenuInflater().inflate(R.menu.menu_report, menu);
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
        case R.id.menu_submit:
        	peekAtJson(createJSONObject());
        	return true;
        case R.id.menu_legal:
        	showDialog(LEGAL);
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
	
	public void onEdit(View view) {
		Intent editIntent = new Intent(this, Report_Mapview.class);
		editIntent.putExtra("location", currentEditableLocation);
		startActivityForResult(editIntent, EDIT_REQUEST);
	}
	
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_checkin)).getMap();
			if (mMap != null)
				setUpMap(); // Weird method chaining, but this is what google example code does
		}
	}
	
	private void setUpMap() {
        updateMyLocation();
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setAllGesturesEnabled(false);
    }
	
	private void updateMyLocation() {
		mMap.setMyLocationEnabled(true);
	}
	
	private void updateUILocation(Location location) {
		goToCurrentPosition(location);
		setEditTexts(location.getLatitude(), location.getLongitude());
		if (!mapHasMarker)
			setCurrentPositionMarker(location);
	}
	
	// Method won't be called unless Play APK in installed
	public void wheresWaldo() {
		Location gpsLocation = requestUpdatesFromProvider();
		if (gpsLocation == null)
			Toast.makeText(getApplicationContext(), "No GPS signal", Toast.LENGTH_SHORT).show();
		else if (gpsLocation != null) {
			// Set global location variable so if user selects edit, it has something to pass
			currentEditableLocation = gpsLocation;
			updateUILocation(gpsLocation);
		}
	}
	
	private Location requestUpdatesFromProvider() {
		Location location = null;
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Also set global location variable so if user selects edit, it has something to pass
            currentEditableLocation = location;
        }
		return location;
	}
	
	private void buildAlertMessageNoGps() {
		showDialog(NO_GPS);
	}
	
	private void setEditTexts(double latitude, double longitude) {
		tvlat.setText("Latitude:\t\t\t" + latitude);
		tvlong.setText("Longitude:\t\t" + longitude);
	}
	
	public void setCurrentPositionMarker(Location location) {
		// Create new LatLng object
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		
		// Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
        mapHasMarker = true;
	}
	
	public void goToCurrentPosition(Location location) {
		if (!checkReady()) {
			return;
		}
		
		// Taken from google sample code
		userCurrentPosition =
	            new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location.getLongitude()))
	                    .zoom(18f) //arbitrary
	                    .bearing(0)
	                    .tilt(35) //arbitrary
	                    .build();
		
		changeCamera(CameraUpdateFactory.newCameraPosition(userCurrentPosition));
	}
	
	// Part of the sample code
	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, "Map not loaded yet", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void changeCamera(CameraUpdate update) {
		changeCamera(update, null);
	}
	
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		mMap.animateCamera(update, callback);
//		mMap.moveCamera(update); //for the less fun people
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case LEGAL:
				return new AlertDialog.Builder(this)
				.setTitle("Legal Notice")
				.setMessage(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext()))
				.setPositiveButton("Got it", null)
				.setNegativeButton("Cancel", null)
				.create();
			case CAMERA:
				return new AlertDialog.Builder(this)
				.setItems(R.array.camera_array, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) { // Take picture
							Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
						} else if (item == 1) { // Choose from gallery
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
						}
					}
				})
				.create();
			case NO_GPS:
				return new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.gps_is_disabled))
				.setMessage(getResources().getString(R.string.show_location_settings))
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
		//http://stackoverflow.com/questions/3326366/what-context-should-i-use-alertdialog-builder-in
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // User took a picture with the camera
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            ib.setImageBitmap(photo);
            
		} else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
            InputStream imageStream = null;
			
            try { imageStream = getContentResolver().openInputStream(selectedImage); }
			catch (FileNotFoundException e) {}
            
			Bitmap img = BitmapFactory.decodeStream(imageStream);
			ib.setImageBitmap(img);
			
        } else if (requestCode == EDIT_REQUEST && resultCode == RESULT_OK) {
        	editedCoordinatesInOtherActivitySoDontGetGPSLocation = true;
        	Location fixedLocation = data.getExtras().getParcelable("location");
        	
        	// Current marker is expired, remove that crap
        	mMap.clear();
        	mapHasMarker = !mapHasMarker;
        	setUpMapIfNeeded();
        	updateUILocation(fixedLocation);
        	
        	// Update location to be send with JSON report
        	currentEditableLocation.setLatitude(fixedLocation.getLatitude());
        	currentEditableLocation.setLongitude(fixedLocation.getLongitude());
        	
        	Toast.makeText(getApplicationContext(), "new position set", Toast.LENGTH_SHORT).show();
        }
    }
	
	
	// Sample json object will look like:
//	{
//	  "user": 
//	  {
//	    "username":"trogdor"
//		"name":"slender"
//		"percentage", "2 percent"
//	  }
//	}
	private JSONObject createJSONObject() {
		Time now = new Time();
		now.setToNow();
		JSONObject parent = new JSONObject();
		JSONObject report = new JSONObject();
		try {
			report.put("username", "trogdor");
			report.put("name", "slender");
			report.put("percentage", "2 percent");
			report.put("squaretype", sp.getSelectedItem().toString());
			report.put("squarenum", etarea.getText());
			report.put("lat", currentEditableLocation.getLatitude());
			report.put("long", currentEditableLocation.getLongitude());
			report.put("notes", notes.getText());
			report.put("date", now.year);
			//bitmap
			report.put("age", "100");
			
			parent.put("user", report);
			Toast.makeText(getApplicationContext(), now.year + "-" + (now.month + 1) + "-" + now.monthDay, Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Data not saved, try again", Toast.LENGTH_SHORT).show();
		}
		return parent;
	}
	
	// Used for testing
	private void peekAtJson(JSONObject json) {
		try {
			JSONObject itemObject = json.getJSONObject("user");
			String lol = itemObject.getString("username");
			Toast.makeText(getApplicationContext(), lol + " ", Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_SHORT).show();
		}
	}

}
