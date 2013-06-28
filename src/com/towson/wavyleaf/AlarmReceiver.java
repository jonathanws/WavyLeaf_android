package com.towson.wavyleaf;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
	
	Context ctx;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;
		showNotification(context);
	}
	
	private void showNotification(Context context) {
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, Sighting.class), 0);

	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	    		.setAutoCancel(true)
	    		.setContentIntent(contentIntent)
	    		.setContentText("Record your next point.")
	    		.setContentTitle("Timing interval elapsed")
	            .setSmallIcon(R.drawable.ic_notification)
	            .setWhen(System.currentTimeMillis());
	    
	    if (sp.getBoolean(Settings.KEY_CHECKBOX_NOISE, true))
        	mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
        if (sp.getBoolean(Settings.KEY_CHECKBOX_VIBRATE, true))
        	mBuilder.setVibrate(vibrationPattern());
	    
	    nm.notify(Main.notifTripID, mBuilder.build());
	}
	
	protected long[] vibrationPattern() {
		int buzz = 150;
		int gap = 100;
		long[] pattern = {0, buzz, gap, buzz};
		return pattern;
	}
	
}