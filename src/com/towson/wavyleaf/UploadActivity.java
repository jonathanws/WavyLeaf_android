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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class UploadActivity extends SherlockActivity {
	
	private boolean success = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_uploadactivity);
        
        if (!isDBEmpty())
        	uploadPoints();
        finish();
        Toast.makeText(getApplicationContext(), "Sightings uploaded", Toast.LENGTH_SHORT).show();
    }
	
	// http://stackoverflow.com/questions/11251901/check-whether-database-is-empty
	protected boolean isDBEmpty() {
		DatabaseListJSONData m_dbListData = new DatabaseListJSONData(this);
		SQLiteDatabase db = m_dbListData.getWritableDatabase();
		
		Cursor cur = db.rawQuery("SELECT * FROM " + DatabaseConstants.TABLE_NAME, null);
		if (cur.moveToFirst())
			return false;
		else
			return true;
	}
    
    protected void uploadPoints() {
    	DatabaseListJSONData m_dbListData = new DatabaseListJSONData(this);
		SQLiteDatabase db = m_dbListData.getWritableDatabase();
		JSONObject jobj = null;
		
		// I want to look at the entire database
		Cursor c = db.rawQuery("SELECT * FROM " + DatabaseConstants.TABLE_NAME, null);
		
		if (c != null) {
			
			// Look at the first entry
			c.moveToFirst();
			
			while (c.moveToNext()) {
				
				// Get the blob
				
				// This is very hacky, and very incorrect
				int iRow = c.getColumnIndex(DatabaseConstants.TABLE_NAME) + 2;
				String result = c.getString(iRow);

				// But it works

				// Convert to json
				jobj = stringToJSON(result);
				
				// Dirty work
				new Upload(this, UploadData.TASK_SUBMIT_POINT).execute(jobj);
				
			}
		}
    }
    
    protected JSONObject stringToJSON(String s) {
    	JSONObject jo = null;
    	try {
    		jo = new JSONObject(s);
    	} catch (JSONException e) {
    		e.printStackTrace();
    	}
    	return jo;
    }
    
    public class Upload extends AsyncTask<JSONObject, Void, String> {
    	
    	private ProgressDialog pd;
    	private Context c;
    	protected int task = 0;
    	
    	public Upload(Context ctx, int which) {
    		this.c = ctx;
    		this.task = which;
    		this.pd = new ProgressDialog(c);
    	}
    	
    	protected void onPreExecute() {
    		this.pd.setMessage("Uploading...");
    		this.pd.show();
    	}
    	
    	@Override
		protected String doInBackground(JSONObject... jobj) {
    		
    		String result = "";
			
    		// Verify that we know which PHP script to submit to
    		if ((this.task == UploadData.TASK_SUBMIT_POINT) || (this.task == UploadData.TASK_SUBMIT_USER)) {
    			
    			// Read in the JSONObject from the JSONObject array
    			if (jobj.length > 0) {
    				final JSONObject json = jobj[0];
    				
    				// Create a new HttpClient and Post Header
    				HttpClient hc = new DefaultHttpClient();
    				HttpPost hp = new HttpPost(UploadData.SERVER_URL + getHttpPost());
    				
    				try {
    					StringEntity se = new StringEntity(json.toString(), "UTF-8");
    					
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
    						
    						result = sb.toString();
    						
    						if (sb.toString().contains("1")) {
    							success = true;
    							deleteFirstEntry();
    						} else
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
    		if (this.pd.isShowing())
    			this.pd.dismiss();
    	}
    	
    	protected String getHttpPost() {
    		if (this.task == UploadData.TASK_SUBMIT_USER)
    			return UploadData.SUBMIT_USER;
    		else if (this.task == UploadData.TASK_SUBMIT_POINT)
    			return UploadData.SUBMIT_POINT;
    		return null;
    	}
    	
    	protected void deleteFirstEntry() {
    		DatabaseListJSONData m_dbListData = new DatabaseListJSONData(this.c);
    		SQLiteDatabase db = m_dbListData.getWritableDatabase();
    		
    		String ALTER_TBL ="delete from " + DatabaseConstants.TABLE_NAME +
    			     " where " + DatabaseConstants._ID + 
    			     " in (select "+ DatabaseConstants._ID + 
    			     " from " + DatabaseConstants.TABLE_NAME + " order by _id LIMIT 1);";

    		db.execSQL(ALTER_TBL);
    	}

		
    }
    
}