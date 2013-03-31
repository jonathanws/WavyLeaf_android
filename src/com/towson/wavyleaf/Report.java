package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private static final int CAMERA_REQUEST = 1337;
	private boolean gpsEnabled = false;
	private boolean playAPKEnabled = false;
	private boolean mapHasMarker = false; // onResume keeps adding markers to map, this should stop it
	protected GoogleMap mMap;
	private UiSettings mUiSettings;
	protected ImageButton ib;
	protected RadioGroup rg;
	protected TextView tvlat, tvlong, tvpicnotes, tvper, tvcoor, tvarea;
	protected EditText notes;
	protected ToggleButton b1, b2, b3, b4, b5, b6;
	protected LocationManager mLocationManager;
	protected CameraPosition userCurrentPosition;
	protected Spinner sp;
	
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
		tvcoor = (TextView) findViewById(R.id.tv_coordinates);
		tvarea = (TextView) findViewById(R.id.tv_areainfested);
		notes = (EditText) findViewById(R.id.notes);
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
		
		ib.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(CAMERA);
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
		tvper.setTypeface(tf_bold);
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
	}
	
	public void onEdit(View view) {
		Intent editIntent = new Intent(this, Report_Mapview.class);
		startActivity(editIntent);
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
		Location gpsLocation = null;
		gpsLocation = requestUpdatesFromProvider();
		
		if (gpsLocation != null)
			updateUILocation(gpsLocation);
	}
	
	private Location requestUpdatesFromProvider() {
		Location location = null;
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
		return location;
	}
	
	private void buildAlertMessageNoGps() {
		showDialog(NO_GPS);
	}
	
	private void setEditTexts(double latitude, double longitude) {
		tvlat.setText("Latitude:\t\t" + latitude);
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
//		if (!checkReady()) {
//			return;
//		}
		
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
						if (item == 0) {
							Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
						}
						else if (item == 1)
							Toast.makeText(getApplicationContext(), "Choose from gallery", Toast.LENGTH_SHORT).show();
					}
				})
				.create();
			case NO_GPS:
				return new AlertDialog.Builder(this)
				.setTitle("GPS is diabled")
				.setMessage("Show location settings?")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            ib.setImageBitmap(photo);
        }  
    }

}
