package com.shark.metroreminder;
 
import java.util.HashMap;

import android.app.Application;
import android.text.format.Time;

public class AppData extends Application{
	
	private HashMap<String, Time> mylist;  
	
	
	public void addIgnoreStation(String stationName){
		stationName = stationName.toLowerCase();
		Time curTime = new Time();
		curTime.setToNow(); 
		mylist.put(stationName, curTime);
	}
	public Boolean needIgnore(String stationName)
	{
		Boolean ignoreNotify = false;
		stationName = stationName.toLowerCase();
		Time curTime = new Time();
		curTime.setToNow(); 
	    if(mylist.containsKey(stationName))
	    {
	       Time oldTime = mylist.get(stationName);   
    	   long diff = curTime.toMillis(true) - oldTime.toMillis(true);//这样得到的差值是微秒级别
    	   //long days = diff / (1000 * 60 * 60 * 24);
    	   //long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
    	   //long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);

    	   long expireMinutes = diff / (1000 * 60);
    	   if(expireMinutes<2)//小于两分钟,则忽略提醒
    		   ignoreNotify= true;
    	   	
    	   else {
    		   ignoreNotify= false;
    	   }
	    }
//	    if(!ignoreNotify)//如果大于两分钟,已过期,则需要提醒,并且更新过期时间
//	    {
//	    	addIgnoreStation(stationName);
//	    }
		return ignoreNotify;
	}
	@Override
	public void onCreate(){
		mylist = new HashMap<String,Time>();
		super.onCreate();
	}
}