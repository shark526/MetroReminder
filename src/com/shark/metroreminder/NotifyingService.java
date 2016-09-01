package com.shark.metroreminder;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

import android.app.*;
import android.content.*;
import android.os.*;
import android.telephony.*;
import android.telephony.gsm.*;
import android.text.*;
import android.preference.*;
import android.telephony.cdma.*;

	/**
	 * This is an example of service that will update its status bar balloon 
	 * every 5 seconds for a minute.
	 * 
	 */
public class NotifyingService extends Service {

		// Use a layout id for a unique identifier
	private static int MOOD_NOTIFICATIONS = R.layout.activity_message;
    SharedPreferences settings;
    private AppData appData;
    Boolean vibrateNotify;
    Boolean ringtoneNotify;
	
    // variable which controls the notification thread
    private ConditionVariable mCondition;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        vibrateNotify  = settings.getBoolean("check_vibrate", true);
        ringtoneNotify  = settings.getBoolean("check_ringtone", true);
			// Start up the thread running the service.  Note that we create a
			// separate thread because the service normally runs in the process's
			// main thread, which we don't want to block.
			Thread notifyingThread = new Thread(null, mTask, "NotifyingService");
			mCondition = new ConditionVariable(false);
			notifyingThread.start();
		}

		@Override
		public void onDestroy() {
			// Cancel the persistent notification.
			mNM.cancel(MOOD_NOTIFICATIONS);
			// Stop the thread from generating further notifications
			mCondition.open();
		}

		private Runnable mTask = new Runnable() {
			public void run() {
				//int i=1;
				while(1==1){
					//showNotification("test"+i,i);
					checkCellID();
					if (mCondition.block(5 * 1000)) 
						break;		 
				}
				// Done with our work...  stop the service!
				NotifyingService.this.stopSelf();
			}
		};

		@Override
		public IBinder onBind(Intent intent) {
			return mBinder;
		}

	private void showNotification(String StationName,int CellID) {
		//mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		Intent i = new Intent(this, MessageActivity.class);
		i.putExtra("pStationName", StationName); 
		i.putExtra("pCellId", CellID+""); 
		//PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,Intent.FLAG_ACTIVITY_NEW_TASK);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,PendingIntent.FLAG_CANCEL_CURRENT);
		//mNotification.setLatestEventInfo(this,getResources().getString(R.string.app_name), StationName + " 到了!", pendingIntent);
		//mManager.notify(0, mNotification);

		Notification notification = new Notification.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setWhen(System.currentTimeMillis())
			.setContentTitle(getResources().getString(R.string.app_name))
			.setContentText(StationName + " 到了!")  // the contents of the entry
			.setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
			.build();
		//notification.defaults=Notification.DEFAULT_ALL;

        //if(vibrateNotify && ringtoneNotify)
        //    notification.defaults = Notification.DEFAULT_ALL;
        //else if(vibrateNotify)
        //    notification.defaults = Notification.DEFAULT_VIBRATE;
        if(ringtoneNotify)
            notification.defaults = Notification.DEFAULT_SOUND;
        else
            notification.defaults = Notification.DEFAULT_LIGHTS;
	
			mNM.notify(0,notification);
		//振动
		if(vibrateNotify){
			Vibrator vibrator = (Vibrator)this.getSystemService(VIBRATOR_SERVICE); 
			vibrator.vibrate(2000);
		}
	}
	private void checkCellID()
	{

		try{

			String stationName = "You have new message!";
			/*TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			
            String operator = mTelephonyManager.getSimOperator();
			int cellId = 0;
            if(operator!=null){
//             
                if(operator.equals("46003")){
                    //中国电信
                    CdmaCellLocation location1 = (CdmaCellLocation)mTelephonyManager.getCellLocation();
                    cellId = location1.getBaseStationId();
                }
                else{
                    // 中国移动和中国联通获取LAC、CID的方式
                    GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
                    cellId = location.getCid();

                }

            }*/
			
			
			String currentCellID = NetworkUtils.getCurrentCellID(NotifyingService.this);

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
					showNotification(stationName,Integer.parseInt(currentCellID));
				}
			}

		}
		catch(Exception ex)
		{

		}






	}
		// This is the object that receives interactions from clients.  See
		// RemoteService for a more complete example.
		private final IBinder mBinder = new Binder() {
			@Override
			protected boolean onTransact(int code, Parcel data, Parcel reply,
										 int flags) throws RemoteException {
				return super.onTransact(code, data, reply, flags);
			}
		};

		private NotificationManager mNM;
	}
