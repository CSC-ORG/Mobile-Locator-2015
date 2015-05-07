package com.example.silentphone;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;



public class ProximityActivity extends Activity {
	
	String notificationTitle;
	String notificationContent;
	String tickerMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		boolean proximity_entering = getIntent().getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
		
		double lat = getIntent().getDoubleExtra("lat", 0);
		
		double lng = getIntent().getDoubleExtra("lng", 0);
		
		String strLocation = Double.toString(lat)+","+Double.toString(lng);
		
		if(proximity_entering){
			Toast.makeText(getBaseContext(),"Entering the region"  ,Toast.LENGTH_LONG).show();
			notificationTitle = "Proximity - Entry";
			notificationContent = "Entered the region:" + strLocation;
			tickerMessage = "Entered the region:" + strLocation;
			AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}else{
			Toast.makeText(getBaseContext(),"Exiting the region"  ,Toast.LENGTH_LONG).show();
			notificationTitle = "Proximity - Exit";
			notificationContent = "Exited the region:" + strLocation;
			tickerMessage = "Exited the region:" + strLocation;
			AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
		
		
		
		Intent notificationIntent = new Intent(getApplicationContext(),NotificationView.class);
		notificationIntent.putExtra("content", notificationContent );
		notificationIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
							.setWhen(System.currentTimeMillis())
							.setContentText(notificationContent)
							.setContentTitle(notificationTitle)
							.setSmallIcon(R.drawable.ic_launcher)
							.setAutoCancel(true)
							.setTicker(tickerMessage)
							.setContentIntent(pendingIntent)
							.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		Notification notification = notificationBuilder.build();
		nManager.notify((int)System.currentTimeMillis(), notification);
		finish();
		
	}
}
