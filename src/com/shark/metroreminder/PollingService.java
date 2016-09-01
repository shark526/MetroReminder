package com.shark.metroreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
/**
 * Polling service 
 * @Author Ryan
 * @Create 2013-7-13 上午10:18:44
 */
public class PollingService extends Service {

	public static final String ACTION = "com.shark.service.PollingService";
	private AppData appData;
	
	private Notification mNotification;
	private NotificationManager mManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		initNotifiManager();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		new PollingThread().start();
	}

	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		mNotification = new Notification();
		mNotification.icon = icon;
		mNotification.tickerText = "New Message";
		mNotification.defaults |= Notification.DEFAULT_SOUND;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}

	@SuppressWarnings("deprecation")
	private void showNotification(String StationName,int CellID) {
		mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MessageActivity.class);
		i.putExtra("pStationName", StationName); 
		i.putExtra("pCellId", CellID+""); 
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,Intent.FLAG_ACTIVITY_NEW_TASK);
 
		//mNotification.setLatestEventInfo(this,getResources().getString(R.string.app_name), StationName + " 到了!", pendingIntent);
		//mManager.notify(0, mNotification);
		
		Notification notification = new Notification.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			
			.setWhen(System.currentTimeMillis())
			.setContentTitle(getResources().getString(R.string.app_name))
			.setContentText(StationName + " 到了!")  // the contents of the entry
			.setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
			.build();
		mManager.notify(0, notification);
		//振动
		Vibrator vibrator = (Vibrator)this.getSystemService(VIBRATOR_SERVICE); 
		vibrator.vibrate(2000);
	}
	 private void checkCellID()
	 {

		try{

			String stationName = "You have new message!";
			TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
			int cellId = location.getCid();
			String currentCellID = cellId + "";

			SharedPreferences shared = this.getSharedPreferences("info",
					Context.MODE_WORLD_READABLE );

			Boolean findCellID = false;

			if(shared.getAll().containsKey(currentCellID))
			{
				stationName = shared.getString(currentCellID,"");
				if(stationName.contains("|1"))
				{
					stationName = stationName.split("\\|")[0];
					findCellID = true;
				}
			}

			if(findCellID)
			{
				appData = (AppData) getApplication();
				Boolean needIgnore = appData.needIgnore(stationName);
				if(!needIgnore)
				{
					showNotification(stationName,cellId);
				}
			}
			
		}
		catch(Exception ex)
		{
			
		}
        
          
	     
	    
	    
		 
	 }

	/**
	 * Polling thread
	 * @Author Ryan
	 * @Create 2013-7-13 上午10:18:34
	 */
	int count = 0;
	class PollingThread extends Thread {
		@Override
		public void run() {
			System.out.println("Polling...");
			count ++;
			if (count % 5 == 0) {
				checkCellID();
				//showNotification();
				System.out.println("New message!");
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("Service:onDestroy");
	}

}
