package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//TODO obtain debug map key
//TODO (later) obtain signature map key
//TODO Do we want each marker to have a bubble? I'd say no (JS)
//TODO Decide which text to use in layout (compare "picture/notes" to "percentage seen"
//TODO Use network or GPS?

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
	
	public static final int LEGAL = 1, MAPTYPE = 2; // Used for calling dialogs. arbitrary numbers
	protected GoogleMap mMap;
	private UiSettings mUiSettings;
	protected TextView tvlat, tvlong, tvpic, tvper, tvcoor;
	protected ToggleButton b1, b2, b3, b4, b5, b6;
	protected RadioGroup rg;
	protected LocationManager lm;
	protected CameraPosition userCurrentPosition;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_report);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		init();
		// Most setup methods are in onResume()
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		Typeface tf_bold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
		
		tvlat = (TextView) findViewById(R.id.tv_latitude);
		tvlong = (TextView) findViewById(R.id.tv_longitude);
		tvpic = (TextView) findViewById(R.id.tv_picturenotes);
		tvper = (TextView) findViewById(R.id.tv_percentageseen);
		tvcoor = (TextView) findViewById(R.id.tv_coordinates);
		b1 = (ToggleButton) findViewById(R.id.bu_1);
		b2 = (ToggleButton) findViewById(R.id.bu_2);
		b3 = (ToggleButton) findViewById(R.id.bu_3);
		b4 = (ToggleButton) findViewById(R.id.bu_4);
		b5 = (ToggleButton) findViewById(R.id.bu_5);
		b6 = (ToggleButton) findViewById(R.id.bu_6);
		rg = ((RadioGroup) findViewById(R.id.toggleGroup));
		
		//set all the beautiful typefaces
		tvlat.setTypeface(tf_light);
		tvlong.setTypeface(tf_light);
		tvpic.setTypeface(tf_bold);
		tvper.setTypeface(tf_bold);
		tvcoor.setTypeface(tf_bold);
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
		setUpMapIfNeeded();
		if (doesDeviceHaveGooglePlayServices()) {
			wheresWaldo(true);
			setDragListener();
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
	
	// When the user drags the marker around the page, the textviews will change in real time--cool!
	protected void setDragListener() {
		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDrag(Marker marker) {
				LatLng draggedLatLng = marker.getPosition();
				double lat = draggedLatLng.latitude;
				double lng = draggedLatLng.longitude;
				setEditTexts(lat, lng);
			}
			@Override public void onMarkerDragEnd(Marker marker) {}
			@Override public void onMarkerDragStart(Marker marker) {}
		});
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
//        case android.R.id.home:
//        	Intent mainIntent = new Intent(this, Main.class);
//            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(mainIntent);
//            finish();
//            return true;
		case R.id.menu_maptype:
			showDialog(MAPTYPE);
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
        mUiSettings.setMyLocationButtonEnabled(false); // Currently overlapped by zoom buttons
    }
	
	private void updateMyLocation() {
		mMap.setMyLocationEnabled(true);
	}
	
	public void wheresWaldo(boolean hasPlayAPK) {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //TODO network or gps?
		
		// Own method so they can be updated in real time
		setEditTexts(location.getLatitude(), location.getLongitude());
		
		if (hasPlayAPK) { // Crashes otherwise
			goToCurrentPosition(location.getLatitude(), location.getLongitude());
			setCurrentPositionMarker(location.getLatitude(), location.getLongitude());
		}
	}
	
	private void setEditTexts(double latitude, double longitude) {
		tvlat.setText("Latitude:\t" + latitude);
		tvlong.setText("Longitude:\t" + longitude);
	}
	
	public void goToCurrentPosition(double latitude, double longitude) {
		if (!checkReady()) {
			return;
		}
		
		// Taken from google sample code
		userCurrentPosition =
	            new CameraPosition.Builder()
						.target(new LatLng(latitude, longitude))
	                    .zoom(13f) //arbitrary
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
	
	public void setCurrentPositionMarker(double latitude, double longitude) {		
		// Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        
        // Create new LatLng object
        LatLng latLng = new LatLng(latitude, longitude);

        // Setting the position for the marker
        markerOptions.position(latLng);
        
        // So user can edit marker
        markerOptions.draggable(true);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        
        // TODO - Do we need this?  Won't all entries need a title?
        // Maybe this could be date?  (JS)
        markerOptions.title("3/18/13 - nth entry");

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
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
			case MAPTYPE:
				return new AlertDialog.Builder(this)
				.setItems(R.array.maptype_array, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0)
							mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
						else if (item == 1)
							mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
		//http://stackoverflow.com/questions/3326366/what-context-should-i-use-alertdialog-builder-in
	}

}
