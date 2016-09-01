package com.shark.metroreminder;

import android.content.*;
import android.telephony.*;
import android.telephony.cdma.*;
import android.telephony.gsm.*;

public class NetworkUtils
{
	public static String getCurrentCellID(Context ct){
	    String  currentCellID = "";
		try{
            int cellId = 0;
            TelephonyManager mTelephonyManager = (TelephonyManager) ct.getSystemService(Context.TELEPHONY_SERVICE);
            String operator = mTelephonyManager.getSimOperator();
            if(operator!=null){
//                if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
//                    //中国移动
//                }else if(operator.equals("46001")){
//                    //中国联通
//                }else
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

            }

		    currentCellID = cellId + ""; 
		}
		catch(Exception ex){
	    	//Toast.makeText(MainActivity.this, "获取网络CellID异常!", Toast.LENGTH_LONG).show(); 
	    	currentCellID="";
		}
		return currentCellID;
	}
}
