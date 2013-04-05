package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class Trip extends SherlockActivity {
	
	private static final int ONSTART = 6;
	protected TextView tripInterval, tripSelection, tally, tallyNumber;
	protected Button doneTrip, donePoint;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_trip);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		showDialog(ONSTART);
		init();
	}
	
	private void init() {
		Typeface tf_light = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
		Typeface tf_bold = Typeface.createFromAsset(getAssets(), "fonts/roboto_bold.ttf");
		
		tripInterval = (TextView) findViewById(R.id.tv_tripinterval);
		tripSelection = (TextView) findViewById(R.id.tv_tripselection);
		tally = (TextView) findViewById(R.id.tv_triptally);
		tallyNumber = (TextView) findViewById(R.id.tv_triptallynumber);
		doneTrip = (Button) findViewById(R.id.doneTrip);
		donePoint = (Button) findViewById(R.id.donePoint);
		
		tripInterval.setTypeface(tf_light);
		tripSelection.setTypeface(tf_light);
		tally.setTypeface(tf_light);
		tallyNumber.setTypeface(tf_light);
		doneTrip.setTypeface(tf_light);
		donePoint.setTypeface(tf_light);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case ONSTART:
				return new AlertDialog.Builder(this)
				.setTitle("Choose Interval")
				.setItems(R.array.tripinterval_array, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0)
							Toast.makeText(getApplicationContext(), "Five Minutes", Toast.LENGTH_SHORT).show();
					}
				})
				.create();
		}
		return super.onCreateDialog(id);
	}

}
