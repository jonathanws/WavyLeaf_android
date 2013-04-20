package com.towson.wavyleaf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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
import android.widget.AdapterView.OnItemSelectedListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Trip extends SherlockActivity {
	
	private static final int CAMERA = 3;
	protected TextView tripInterval, tripSelection, tally, tallyNumber, tvlat, tvlong, tvpicnotes, 
		tvper, tvper_summary, tvcoor, tvarea, tvarea_summary;
	protected EditText notes, etarea;
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
		save = (Button) findViewById(R.id.save);
		
		// Listener for camera button
		ib.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(CAMERA);
			}
		});
		
		// Listener for EditText in Area Infested
		etarea.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etarea.getText().length() == 0) {
					tvarea_summary.setText("");
				}
				else if(etarea.getText().toString().contains("-")) {	//negative number sign
					etarea.getEditableText().clear();
					Toast.makeText(getApplicationContext(), "Negative values not allowed!", Toast.LENGTH_SHORT).show();
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
		save.setTypeface(tf_light);
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
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		tripInterval.setText(sp.getString("TRIP_INTERVAL", "(Not Set)"));	
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
		
	public void onSaveButtonClick(View view) {
		finish();
	}

}
