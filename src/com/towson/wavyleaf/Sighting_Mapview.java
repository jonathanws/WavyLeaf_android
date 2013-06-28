package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
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

public class Sighting_Mapview extends SherlockFragmentActivity implements OnClickListener {
	
	private static final int LEGAL = 1, MAPTYPE = 2; // Used for calling dialogs. arbitrary numbers
	private boolean mapHasMarker = false; // onResume keeps adding markers to map, this should stop it
	private UiSettings mUiSettings;
	protected TextView tvlat, tvlong;
	protected Button reset, done;
	protected GoogleMap mMap;
	protected Marker marker;
	protected CameraPosition userCurrentPosition;
	protected Location receivedLocation, newLocation;
	protected Intent in;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x90000000));
		setContentView(R.layout.layout_sighting_mapview);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Most setup methods are in onResume()
		init();
	}
	
	protected void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		tvlat = (TextView) findViewById(R.id.tv_latitude);
		tvlong = (TextView) findViewById(R.id.tv_longitude);
		reset = (Button) findViewById(R.id.btnResetCoord);
		done = (Button) findViewById(R.id.btnDone);
		reset = (Button) findViewById(R.id.btnResetCoord);
		done = (Button) findViewById(R.id.btnDone);
		
		//set all the beautiful typefaces
		tvlat.setTypeface(tf_light);
		tvlong.setTypeface(tf_light);
		reset.setTypeface(tf_light);
		done.setTypeface(tf_light);
		
		reset.setOnClickListener(this);
		done.setOnClickListener(this);	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setUpMapIfNeeded();
		
		// Get location from Sighting.java. Default to this on button click
		in = getIntent();
		receivedLocation = in.getExtras().getParcelable("location");
		if (receivedLocation != null)
			updateUILocation(receivedLocation);
		
		setDragListener();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	getSupportMenuInflater().inflate(R.menu.menu_sighting_mapview, menu);
    	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
        	Intent mainIntent = new Intent(this, Sighting.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
            return true;
		case R.id.menu_maptype:
			showDialog(MAPTYPE);
			return true;
        case R.id.menu_legal:
        	showDialog(LEGAL);
        	return true;
        }
		return super.onOptionsItemSelected(item);
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
		mUiSettings.setCompassEnabled(false);
	}
	
	private void updateMyLocation() {
		mMap.setMyLocationEnabled(true);
	}
	
	private void updateUILocation(Location location) {
		// Move the camera to this point
		goToCurrentPosition(location);
		
		// Set a marker at this point
		if (!mapHasMarker)
			setCurrentPositionMarker(location);
		
		// Set EditTexts
		setEditTexts(location.getLatitude(), location.getLongitude());
		
	}
	
	private void setEditTexts(double latitude, double longitude) {
		tvlat.setText("Latitude:\t\t\t\t" + latitude);
		tvlong.setText("Longitude:\t\t" + longitude);
	}
	
	private void grabNewCoordinates() {
		receivedLocation.setLatitude(marker.getPosition().latitude);
		receivedLocation.setLongitude(marker.getPosition().longitude);
	}
	
	public void setCurrentPositionMarker(Location location) {		
		// Create new LatLng object
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		
		// Creating a marker		
		marker = mMap.addMarker(new MarkerOptions()
				.position(latLng)
				.draggable(true)
				.title("Long-Press and drag to edit"));
		marker.showInfoWindow();
		
		mapHasMarker = true;
	}
	
	public void goToCurrentPosition(Location location) {
		if (!checkReady())
			return;
		
		// Taken from google sample code
		userCurrentPosition = new CameraPosition.Builder()
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
			case MAPTYPE:
				return new AlertDialog.Builder(this)
				.setItems(R.array.maptype_array, new DialogInterface.OnClickListener() {
					// Changing view seems to change zoom as well.  We'll account for that
					public void onClick(DialogInterface dialog, int item) {
						float zoomLevel;	//collect zoom level info, then switch map types then reset zoom essentially
						if (item == 0) {
							zoomLevel = mMap.getCameraPosition().zoom;
							mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
							changeCamera(CameraUpdateFactory.zoomTo(zoomLevel));
						}
						else if (item == 1) {
							zoomLevel = mMap.getCameraPosition().zoom;
							mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
							changeCamera(CameraUpdateFactory.zoomTo(zoomLevel));
						}
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
		//http://stackoverflow.com/questions/3326366/what-context-should-i-use-alertdialog-builder-in
	}
	
	public void onClick(View view) {
		if (view == this.reset) {
			mMap.clear();
			mapHasMarker = !mapHasMarker;
			updateUILocation(receivedLocation);
		} else if (view == this.done) {
			grabNewCoordinates();
			in.putExtra("location", receivedLocation);
			setResult(RESULT_OK, in);
			finish();
		}
	}

}
