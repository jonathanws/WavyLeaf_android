package com.towson.wavyleaf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
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
	protected static final String ARE_NOTES = "notes";
	protected static final String ARG_OUTDOOREXPERIENCE = "outdoorexperience";
	protected static final String ARG_PERCENT = "percent";
	protected static final String ARG_USER_ID = "user_id";
	protected static final String ARG_WAVYLEAFID = "wavyleafid";

	protected static final int TASK_SUBMIT_USER = 1;
	protected static final int TASK_SUBMIT_POINT = 2;

	protected int task = 0;
	private Context context;

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

				// Create a new HttpClient and Post Header
				HttpClient hc = new DefaultHttpClient();
				HttpPost hp = new HttpPost(SERVER_URL + getHttpPost());

				try {

					// Data to store
					hp.setEntity(new UrlEncodedFormEntity(getNameValuePairFromJSON(json)));

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

						result = sb.toString();
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
		if (s != null)
			Toast.makeText(this.context, s, Toast.LENGTH_LONG).show();
	}

	protected String getHttpPost() {
		if (this.task == TASK_SUBMIT_USER)
			return SUBMIT_USER;
		else if (this.task == TASK_SUBMIT_POINT)
			return SUBMIT_POINT;
		return null;
	}
	
	// Since we've only figured out how to send a list, this method 
	// will sort through the given JSONObject and create one
	protected List<NameValuePair> getNameValuePairFromJSON(JSONObject jo) {
		
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		
		// Go through JSONObject and make a List
		for (int i = 0; i < jo.names().length(); i++) {
			try {
				nvp.add(new BasicNameValuePair(
						jo.names().getString(i), // Get the key for each parameter
						jo.getString(jo.names().getString(i)))); // Get the value for each key
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return nvp;
		
	}

}
