package com.towson.wavyleaf;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * This service activates six hours after inactivity from 
 * the user if they have points that they haven't uploaded
 */

public class ReminderService extends Service {
	
	private static Timer timer;
	protected static boolean isTimerRunning = false;
	NotificationManager nm;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		startReminderTimer();
	}
	
	//following the answer from
	//http://stackoverflow.com/questions/3819676/android-timer-within-a-service
	private void startReminderTimer() {
		if(isTimerRunning == true) {
			try {
				timer.cancel();
				timer.purge();
			}
			catch(Exception e) { }
		}
		
		if (!isDBEmpty()) {				// If there are unsent points, start the timer again
			timer = new Timer(true);	// Run as daemon thread
			timer.schedule(new mainTask(), 21600000);	//6 hours = 21600000 milliseconds
			isTimerRunning = true;
		} else 							// If all points are sent
			isTimerRunning = false;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(isTimerRunning == true) {
			timer.cancel();
			timer.purge();
			isTimerRunning = false;
		}
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startid) {
		return START_NOT_STICKY;
	}
	
	private class mainTask extends TimerTask {
		
        public void run() {
        	isTimerRunning = false;
        	nm = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        	nm.cancel(Main.notifReminderID);
        	showReminderNotification(getBaseContext());
			//Need to stop service -- calling stopService which is same as stopService(Intent)
			stopSelf();
        }
        
    	private void showReminderNotification(Context context) {
    		
    		// Because android loves to be so helpful in providing non activity-launching buttons
    		// in notifications, I'm just going to spur this into another activity where the user
    		// will upload their points
    		
    		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, UploadActivity.class), Intent.FLAG_ACTIVITY_CLEAR_TOP);

    	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
    	    		.setAutoCancel(true)
    	    		.setContentIntent(contentIntent)
    	    		.setContentText("Trip sightings unsubmitted")
    	    		.setContentTitle("Completed your trip?")
    	            .setSmallIcon(R.drawable.ic_notification)
    	            .setWhen(System.currentTimeMillis());
    	    
    	    if (sp.getBoolean(Settings.KEY_CHECKBOX_NOISE, true))
            	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            
            if (sp.getBoolean(Settings.KEY_CHECKBOX_VIBRATE, true))
            	mBuilder.setVibrate(vibrationPattern());
    	    
    	    nm.notify(216612, mBuilder.build());
    	}
    	
    	protected long[] vibrationPattern() {
    		int buzz = 150;
    		int gap = 100;
    		long[] pattern = {0, buzz, gap, buzz};
    		return pattern;
    	}
        
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
	
}


