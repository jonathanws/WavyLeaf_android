package com.towson.wavyleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
	
	//private static final int ONSTART = 6;
	protected TextView tripInterval, tripSelection, tally, tallyNumber, tvlat, tvlong, tvpicnotes, tvper, tvcoor, tvarea, notes;
	protected Button doneTrip, save, b1, b2, b3, b4, b5, b6;
	protected RadioGroup rg;
	protected Spinner sp;
	protected ImageButton ib;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_trip);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//showDialog(ONSTART);
		init();
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
		tvcoor = (TextView) findViewById(R.id.tv_coordinates);
		tvarea = (TextView) findViewById(R.id.tv_areainfested);
		
		notes = (EditText) findViewById(R.id.notes);
		
//		doneTrip = (Button) findViewById(R.id.finishTrip);	//img should say Save & Done Trip
		save = (Button) findViewById(R.id.save);

		b1 = (ToggleButton) findViewById(R.id.bu_1);
		b2 = (ToggleButton) findViewById(R.id.bu_2);
		b3 = (ToggleButton) findViewById(R.id.bu_3);
		b4 = (ToggleButton) findViewById(R.id.bu_4);
		b5 = (ToggleButton) findViewById(R.id.bu_5);
		b6 = (ToggleButton) findViewById(R.id.bu_6);
		
		rg = (RadioGroup) findViewById(R.id.toggleGroup);
		
		sp = (Spinner) findViewById(R.id.sp_areainfested);
		
		ib = (ImageButton) findViewById(R.id.report_imagebutton);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.areainfested_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		
		//set all the beautiful typefaces
		tripInterval.setTypeface(tf_light);
		tripSelection.setTypeface(tf_light);
		tally.setTypeface(tf_light);
		tallyNumber.setTypeface(tf_light);
//		doneTrip.setTypeface(tf_light);
		save.setTypeface(tf_light);
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
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
//    	getSupportMenuInflater().inflate(R.menu.menu_report, menu);
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
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch(id) {
//			case ONSTART:
//				return new AlertDialog.Builder(this)
//				.setTitle("Choose Interval")
//				.setItems(R.array.tripinterval_array, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if (which == 0)
//							Toast.makeText(getApplicationContext(), "Five Minutes", Toast.LENGTH_SHORT).show();
//					}
//				})
//				.create();
//		}
//		return super.onCreateDialog(id);
//	}

}
