package com.towson.wavyleaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class UploadData extends AsyncTask<JSONObject, Void, String> {

	protected static final String SERVER_URL = "http://skappsrv.towson.edu/";
	protected static final String SUBMIT_USER = "wavyleaf/submit_user.php";
	protected static final String SUBMIT_POINT = "wavyleaf/submit_point.php";

	protected static final String ARG_AREATYPE = "areatype";
	protected static final String ARG_AREAVALUE = "areavalue";
	protected static final String ARG_BIRTHYEAR = "birthyear";
	protected static final String ARG_DATE = "date";
	protected static final String ARG_EDUCATION = "education";
	protected static final String ARG_GENERALPLANTID = "generalplantid";
	protected static final String ARG_LATITUDE = "latitude";
	protected static final String ARG_LONGITUDE = "longitude";
	protected static final String ARG_NAME = "name";
	protected static final String ARG_NOTES = "notes";
	protected static final String ARG_OUTDOOREXPERIENCE = "outdoorexperience";
	protected static final String ARG_PERCENT = "percent";
	protected static final String ARG_USER_ID = "user_id";
	protected static final String ARG_WAVYLEAFID = "wavyleafid";
	protected static final String ARG_EMAIL = "email";

	protected static final int TASK_SUBMIT_USER = 1;
	protected static final int TASK_SUBMIT_POINT = 2;

	private DatabaseListJSONData m_dbListData;
	private Context context;
	protected int task = 0;
	private boolean success = false;

	public UploadData(Context context, int which) {
		this.context = context;
		this.task = which;
	}

	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(JSONObject... jobj) {

		String result = "";

		// Verify that we know which PHP script to submit to
		if ((this.task == TASK_SUBMIT_POINT) || (this.task == TASK_SUBMIT_USER)) {

			// Read in the JSONObject from the JSONObject array
			if (jobj.length > 0) {
				final JSONObject json = jobj[0];
				
				// Before we try to send the JSON, save it to local storage
				submitToLocalStorage(json.toString());

				// Create a new HttpClient and Post Header
				HttpClient hc = new DefaultHttpClient();
				HttpPost hp = new HttpPost(SERVER_URL + getHttpPost());

				try {
					
					// Use this section when wanting to send NameValue Pairs
//					hp.setEntity(new UrlEncodedFormEntity(getNameValuePairFromJSON(json)));
					
					// Use this section to send JSON
					StringEntity se = new StringEntity(json.toString(), "UTF-8");
					
					// It seems that these aren't required, but similar lines are used for iphone
//					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//					hp.setHeader("Content-Type", "application/json");
					
					// Data to store
					hp.setEntity(se);
					
					// Execute the post
					HttpResponse response = hc.execute(hp);

					// For response
					if (response != null) {
						InputStream is = response.getEntity().getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						StringBuilder sb = new StringBuilder();
						String line = null;

						try {
							while ((line = reader.readLine()) != null) {
								sb.append(line + "\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						if (sb.toString().contains("1"))
							success = true;
						else
							success = false;
					}
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else
			result = "ERROR: bad script destination";

		return result;
	}

	protected void onPostExecute(String s) {
		
		// If data was submitted successfully
		if (success == true) {
			
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.context);
			Editor ed = sp.edit();
			
			// Increment tally if this was a point for Trip
			if ((context.getClass() + "").contains("Trip"))
				ed.putInt(Settings.KEY_TRIPTALLY, sp.getInt(Settings.KEY_TRIPTALLY, 0) + 1);
			
			// Increment tally if this was a single point
			else if ((context.getClass() + "").contains("Report"))
				ed.putInt(Settings.KEY_SINGLETALLY, sp.getInt(Settings.KEY_SINGLETALLY, 0) + 1);
			
			ed.commit();
			
		} else
			Toast.makeText(this.context, "Error submitting point. Saved for later.", Toast.LENGTH_LONG).show();
			
		//TODO: remove this? or make use of it?
		if (s.equalsIgnoreCase(""))
			Toast.makeText(this.context, s, Toast.LENGTH_LONG).show();
	}

	protected String getHttpPost() {
		if (this.task == TASK_SUBMIT_USER)
			return SUBMIT_USER;
		else if (this.task == TASK_SUBMIT_POINT)
			return SUBMIT_POINT;
		return null;
	}
	
	/** Inserts a single string (read: JSON) into a database */
	protected void submitToLocalStorage(String JSONString) {
		
		//Initiate database
		m_dbListData = new DatabaseListJSONData(this.context);
		SQLiteDatabase db = m_dbListData.getWritableDatabase();
		
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.ITEM_NAME, JSONString);
        db.insertOrThrow(DatabaseConstants.TABLE_NAME, null, values);
	}
	
	
	
	
	// Since we've only figured out how to send a list, this method 
	// will sort through the given JSONObject and create one
//	protected List<NameValuePair> getNameValuePairFromJSON(JSONObject jo) {
//		
//		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
//		
//		// Go through JSONObject and make a List
//		for (int i = 0; i < jo.names().length(); i++) {
//			try {
//				nvp.add(new BasicNameValuePair(
//						jo.names().getString(i), // Get the key for each parameter
//						jo.getString(jo.names().getString(i)))); // Get the value for each key
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return nvp;
//		
//	}

}
