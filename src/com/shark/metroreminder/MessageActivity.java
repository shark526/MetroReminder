package com.shark.metroreminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends Activity {
	private AppData appData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		//get the income parameters
		Intent intent=getIntent();  
		
        final String curStationName=intent.getStringExtra("pStationName");  
        //String curCellId=intent.getStringExtra("pCellId"); 
 
		 AlertDialog.Builder builder = new Builder(MessageActivity.this);
		 builder.setMessage(curStationName + "到咯!");
		 builder.setTitle("提示");
		 builder.setPositiveButton("我知道了",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			   //TODO: add current cell id into the ignore list
				appData = (AppData) getApplication();
				appData.addIgnoreStation(curStationName);
				dialog.dismiss();
			    MessageActivity.this.finish();
			}
		 });
		  builder.setNegativeButton("主页面", new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  dialog.dismiss();
				  //MessageActivity.this.finish();
				  //TODO: show the main activity

					Intent intent = new Intent();
					
					intent.setClass(MessageActivity.this, MainActivity.class);
					
					startActivity(intent);
				  MessageActivity.this.finish();
			  }
		  });
		  builder.setCancelable(false);
		  builder.create().show();
				  
		
		NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
	}
}
