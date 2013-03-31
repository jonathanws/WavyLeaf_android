package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
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

public class Report_Mapview extends SherlockFragmentActivity {
	
	private static final int LEGAL = 1, MAPTYPE = 2, NO_GPS = 4; // Used for calling dialogs. arbitrary numbers
	private boolean gpsEnabled = false;
	private boolean mapHasMarker = false; // onResume keeps adding markers to map, this should stop it
	private UiSettings mUiSettings;
	protected TextView tvlat, tvlong;
	protected GoogleMap mMap;
	protected CameraPosition userCurrentPosition;
	protected LocationManager mLocationManager;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x90000000));
		setContentView(R.layout.layout_report_mapview);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		init();
		// Most setup methods are in onResume()
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		tvlat = (TextView) findViewById(R.id.tv_latitude);
		tvlong = (TextView) findViewById(R.id.tv_longitude);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//set all the beautiful typefaces
		tvlat.setTypeface(tf_light);
		tvlong.setTypeface(tf_light);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Check for GPS
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		// If GPS is disabled
		if (!gpsEnabled) {
			buildAlertMessageNoGps();
		} else if(gpsEnabled) {
			setUpMapIfNeeded();
			wheresWaldo();
			setDragListener();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getSupportMenuInflater().inflate(R.menu.menu_report_mapview, menu);
    	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        	Intent mainIntent = new Intent(this, Report.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
            return true;
		case R.id.menu_maptype:
			showDialog(MAPTYPE);
			return true;
        case R.id.menu_done:
        	Toast.makeText(getApplicationContext(), "finish activity", Toast.LENGTH_SHORT).show();
        	return true;
        case R.id.menu_legal:
        	showDialog(LEGAL);
        	return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	private void buildAlertMessageNoGps() {
		showDialog(NO_GPS);
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
		
		private void setUpMapIfNeeded() {
			if (mMap == null) {
				mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_fullscreen)).getMap();
				if (mMap != null)
					setUpMap(); // Weird method chaining, but this is what google example code does
			}
		}
		
		private void setUpMap() {
	        updateMyLocation();
	        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	        mUiSettings = mMap.getUiSettings();
	        mUiSettings.setMyLocationButtonEnabled(false);
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
		
		private void setEditTexts(double latitude, double longitude) {
			tvlat.setText("Latitude:\t\t\t\t" + latitude);
			tvlong.setText("Longitude:\t\t" + longitude);
		}
		
		public void setCurrentPositionMarker(Location location) {
			// Create new LatLng object
	        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			
			// Creating a marker
	        MarkerOptions markerOptions = new MarkerOptions();
	        markerOptions.position(latLng);
	        markerOptions.draggable(true);
	        markerOptions.title("3/18/13 - nth entry");

	        // Placing a marker on the touched position
	        mMap.addMarker(markerOptions);
	        mapHasMarker = true;
		}
		
		public void goToCurrentPosition(Location location) {
//			if (!checkReady()) {
//				return;
//			}
			
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
		
		private void changeCamera(CameraUpdate update) {
			changeCamera(update, null);
		}
		
		private void changeCamera(CameraUpdate update, CancelableCallback callback) {
			mMap.animateCamera(update, callback);
//			mMap.moveCamera(update); //for the less fun people
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
						// Changing view seems to change zoom as well.  We'll account for that
						public void onClick(DialogInterface dialog, int item) {
							if (item == 0)
								mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
							else if (item == 1)
								mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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

}
