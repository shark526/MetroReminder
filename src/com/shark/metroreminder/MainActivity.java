package com.shark.metroreminder;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.os.*;
import android.telephony.*;
import android.telephony.cdma.*;
import android.telephony.gsm.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

class MapKeyComparator implements Comparator<String>{

	@Override
	public int compare(String str1, String str2) {
		
		return str1.compareTo(str2);
	}
}
public class MainActivity extends Activity {
	
	private static final String TAG = "GSMCellLocationActivity";  
	private String currentCellID;
	private String currentStation;

	private String searchedCells;
	int screenWidth;
	int screenHeight;
	Boolean isDragging = false;
	Button btnMark;
	AlertDialog markDialog;
	Thread searchThread;
	private ConditionVariable mCondition;
	
	private boolean editMode=false;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			handleDialogSearchCellIDs();
		}
	};
	private Runnable searchTask = new Runnable() {
		public void run() {
			while (1==1)
			{
				handler.sendEmptyMessage(0x123);
				if(mCondition.block(2 * 1000))
					break;
			}
		}
	};
	private void handleDialogSearchCellIDs()
	{
		if(markDialog!=null)
		{
			if(searchedCells.isEmpty())
				searchedCells=currentCellID;
			String curCell = NetworkUtils.getCurrentCellID(MainActivity.this);
			if(!searchedCells.contains(curCell))
			{
				searchedCells += "," +curCell;
				markDialog.setMessage(searchedCells);
			}
		}
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		stopService(new Intent(MainActivity.this, 
							   NotifyingService.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//绑定轮询按钮
		findViewById(R.id.tbtnRemind).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Switch tbtn = (Switch)findViewById(R.id.tbtnRemind);
				//打开或暂停服务
				runOrStopService(tbtn.isChecked());
				//runOrStopService(tbtn.getTextOn().equals(tbtn.getText()));
			}
		});
	    bindDataList();
		initMarkDialog();
		initailBtnMark();
		//initMarkDialog();
	}
	private void initMarkDialog(){
		final EditText input = new EditText(MainActivity.this);
		markDialog = new AlertDialog.Builder(MainActivity.this).setTitle("请输入车站名称")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(input)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					if(mCondition!=null)
						mCondition.open();
					TextView text = null; 
					text = (TextView)findViewById(R.id.textView1);
					currentStation = input.getText().toString();

					text.setText(currentStation + currentCellID);
					if(currentCellID.isEmpty())
						return;

					SharedPreferences shared = MainActivity.this.getSharedPreferences("info",
																					  Context.MODE_WORLD_READABLE ); 
					Editor editor = shared.edit();
					//Log.i(TAG, "保存:" + currentStation+ "-" + currentCellID); 
					if(!shared.contains(currentCellID)){
						editor.putString(currentCellID,currentStation+"|0");

						Log.i(TAG, "保存成功:" + currentStation+ "-" + currentCellID); 
					}
					else { 
						editor.remove(currentCellID);
						editor.putString(currentCellID,currentStation+"|1");
						Toast.makeText(MainActivity.this, "已存在对id：" + currentCellID + "的标记,现已更新", Toast.LENGTH_LONG).show(); 

					}
					// 保证操作的事务完整性
					editor.commit(); 
					Toast.makeText(MainActivity.this, currentStation + " 标记成功！", Toast.LENGTH_LONG).show();
					bindDataList();
				}})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(mCondition!=null)
						mCondition.open();
				}
			})
			.setMessage(currentCellID)
			.setNeutralButton("搜索多个", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked Something so do some stuff */
				}
			})
			.setCancelable(false)
			.create(); 
		
	}
	private void initailBtnMark() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		btnMark = (Button)findViewById(R.id.btnMark);
		btnMark.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					//Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_LONG);
					currentCellID=NetworkUtils.getCurrentCellID(MainActivity.this);
					if(currentCellID.isEmpty())
					return;
					 if(isDragging)
					 isDragging=false;
					 else{
					 showMarkDialog();
					
					}
				}
			});
		/*
		btnMark.setOnTouchListener(new View.OnTouchListener() {
				int lastX, lastY;
				public boolean onTouch(View v, MotionEvent event) {
					int ea = event.getAction();
					switch (ea) {
						case MotionEvent.ACTION_DOWN:
							lastX = (int) event.getRawX();
							lastY = (int) event.getRawY();
							break;
						case MotionEvent.ACTION_MOVE:
							
							int dx = (int) event.getRawX() - lastX;
							int dy = (int) event.getRawY() - lastY;
							int left = v.getLeft() + dx;
							int top = v.getTop() + dy;
							int right = v.getRight() + dx;
							int bottom = v.getBottom() + dy;
							if (left < 0) {
								left = 0;
								right = left + v.getWidth();
							}
							if (right > screenWidth) {
								right = screenWidth;
								left = right - v.getWidth();
							}
							if (top < 0) {
								top = 0;
								bottom = top + v.getHeight();
							}
							if (bottom > screenHeight) {
								bottom = screenHeight;
								top = bottom - v.getHeight();
							}
							v.layout(left, top, right, bottom);
							lastX = (int) event.getRawX();
							lastY = (int) event.getRawY();
							//isDragging = true;
							break;
						case MotionEvent.ACTION_UP:
							break;
					}
					return false;
				}
			});*/
	}
	private void showMarkDialog(){
		markDialog.setMessage(currentCellID);
		markDialog.show();
		markDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
			{
		        @Override
				public void onClick(View v)
				{
					Button btnSearch = (Button)v;
					if(btnSearch.getText()=="搜索多个"){
						btnSearch.setText("停止搜索");
						mCondition = new ConditionVariable(false);
						searchThread = new Thread(null, searchTask, "SearchThread");
						searchedCells = "";
						searchThread.start();
					}
					else{
						btnSearch.setText("搜索多个");
						mCondition.open();
					}

				}
			});
	}
	//启动或暂停服务
	private void  runOrStopService(Boolean bStart) { 
		if(bStart){ 
			System.out.println("Start polling service...");
			startService(new Intent(MainActivity.this, 
									NotifyingService.class));
			//PollingUtils.startPollingService(this, 2, PollingService.class, PollingService.ACTION);
		}
		else{
			System.out.println("Stop polling service...");
			stopService(new Intent(MainActivity.this, 
								   NotifyingService.class));
			//PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
		}

	}

    private void removeSelectedItem(final String stationName, final String stationCellID){
        new AlertDialog.Builder(MainActivity.this).setTitle("确定要删除车站'"+ stationName +"'吗?").setIcon(
                android.R.drawable.ic_dialog_info).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                SharedPreferences shared = MainActivity.this.getSharedPreferences("info",Context.MODE_WORLD_READABLE );
                if(stationCellID.equals("..."))
				{
					for(Map.Entry<String,?> entry :shared.getAll().entrySet())
					{

						String curStationName;
						String preValue = entry.getValue().toString();

						if(preValue.contains("|")) {
					
							curStationName =preValue.split("\\|")[0];
						}
						else
						{
							curStationName = preValue;
						}
						//if((entry.getValue().toString().split("\\|")[0]).indexOf(stationName)==1)
						if(curStationName.equals(stationName))
						{
							shared.edit().remove(entry.getKey().toString()).commit();
						}


					}
				}
				else{
					shared.edit().remove(stationCellID).commit();
				}
				
                Toast.makeText(MainActivity.this, "选中项已删除!", Toast.LENGTH_LONG).show();
                bindDataList();
            }
        }).setNegativeButton("取消", null).show();
    }
	
	private boolean existeStationName(ArrayList<StationItem> stationList,String stationName,boolean remind)
	{
		for(StationItem station:stationList)
		{
			if(station.StationName.equals(stationName))
			{
				station.StationCellID="...";
				if(!remind)
					station.needRemind=false;
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void bindDataList() {
		// TODO Auto-generated method stub
        //绑定XML中的ListView，作为Item的容器
        ListView list = (ListView) findViewById(R.id.listView1);
        SharedPreferences shared = MainActivity.this.getSharedPreferences("info",Context.MODE_PRIVATE );

        //ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        Map<String,?> keys = shared.getAll();

        //Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());

        //sortMap.putAll((Map<String, String>)keys);
        ArrayList<StationItem> stationList = new ArrayList<StationItem>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            StationItem curSI = new StationItem();
            String preValue = entry.getValue().toString();

            if(preValue.contains("|")) {
                curSI.needRemind = preValue.split("\\|")[1].equals("1") ? true : false;
                curSI.StationName =preValue.split("\\|")[0];
            }
            else
            {
                curSI.needRemind = false;
                curSI.StationName = preValue;
            }
			try{
            curSI.StationCellID = entry.getKey().toString();
				if(!editMode && existeStationName(stationList,curSI.StationName,curSI.needRemind)){
					
				}
				else{
					stationList.add(curSI);
				}
          	  //stationList.add(curSI);
			}
			catch(Exception ex){
				//String aa=ex.getCause().toString();
				Editor editor = shared.edit();
				editor.remove(null);
				editor.commit();
			}
        }
		Collections.sort(stationList,new Comparator<StationItem>(){
			public int compare(StationItem obj1,StationItem obj2){
				return obj1.StationName.compareToIgnoreCase(obj2.StationName);
			}
		});


        MyAdapter adapter = new MyAdapter(this, stationList,shared);
        list.setAdapter(adapter);
        //Long click item to delete
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String stationCellID = ((TextView) view.findViewById(R.id.ItemText)).getText().toString();
                String stationName = ((TextView) view.findViewById(R.id.ItemTitle)).getText().toString();

                removeSelectedItem(stationName, stationCellID);
                return true;
            }
        });


    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		isExit.setTitle("系统提示");
		isExit.setMessage("请问您是要?");
		isExit.setButton(AlertDialog.BUTTON_POSITIVE,"退出程序", listener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE,"后台运行", listener);
		isExit.show();
		}
		return false;
	}
	private void alterEditView(MenuItem item)
	{
		editMode=editMode?false:true;
		item.setTitle(editMode?"合并列表":"展开列表");
		bindDataList();
	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
				case AlertDialog.BUTTON_POSITIVE:// "退出程序"
					finish();
					break;
				case AlertDialog.BUTTON_NEGATIVE:// "后台运行"
					moveTaskToBack(false);
					break;
				default:
					break;
			}
		}
	};
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case R.id.action_edit:
				alterEditView( item);
				break;
			case R.id.action_settings:
				showSettingsActivity();
				break;
			case R.id.action_help:
				showHelpMessage();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
//menu setting click
	private void showHelpMessage(){
		String helpText = "●首先，感谢使用;)\r\n";
		helpText += "●在要提醒的车站或快到站时，点击[标记]按钮标记车站\r\n";
		helpText += "●长按已标记车站可删除\r\n";
		helpText += "●如果要在某站提醒，请在已标记车站前打勾\r\n";
		helpText += "●点击右上方的[关闭]按钮，开始提醒服务。再点击则关闭提醒服务\r\n";
		helpText += "●配置项中可配置消息提醒方式\r\n";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(helpText);
		builder.setTitle("帮助");
		builder.create().show();
		
	}
	private void showSettingsActivity() {
		try{
			startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),0);
			
		}
		catch(Exception ex)
		{
			Toast.makeText(MainActivity.this, ex.getCause().toString(), Toast.LENGTH_LONG).show();
		}
		
	}

}
