package com.towson.wavyleaf;

import java.util.TimerTask;
import java.util.Timer;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * This class defines a service -- not an activity.
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
		timer = new Timer(true);	//run as daemon thread
		timer.schedule(new mainTask(), 8000);	//6 hours = 21600000
		isTimerRunning = true;
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
        	nm.cancel(Main.mUniqueId);
        	showReminderNotification(getBaseContext());
			//Need to stop service -- calling stopService which is same as stopService(Intent)
			stopSelf();
        }
        
    	private void showReminderNotification(Context context) {   		
    		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, Main.class), Intent.FLAG_ACTIVITY_CLEAR_TOP);

    	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
    	    		.setAutoCancel(true)
    	    		.setContentIntent(contentIntent)
    	    		.setContentText("Remember to upload your data")
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
}
