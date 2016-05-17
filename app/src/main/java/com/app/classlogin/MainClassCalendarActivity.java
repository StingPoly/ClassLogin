package com.app.classlogin;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.CONTENT;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TIME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPE;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPEICON;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;
import static com.app.classlogin.ClassDataStructure_MainClassTime.CLASS_SERIAL;
import static com.app.classlogin.ClassDataStructure_MainClassTime.TABLE_NAME_TimeInformation;
import static com.app.classlogin.ClassInformationPageActivity.PhotoNoteDir;
import static com.app.classlogin.CommonMethod.LessThanTen;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
//import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
//import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiManager.WifiLock;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.ServiceResponseListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.AlarmClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

//Handshake Port:9898 
//FileTransfer Port:10153

//ActionBar
@SuppressLint("NewApi")
public class MainClassCalendarActivity extends AppCompatActivity implements
		MainClassCalendarFragment.HandingDateSeletedListener, PeerListListener,
		ChannelListener, ConnectionInfoListener, GroupInfoListener{

	public static final int HANDSHAKE_PORT = 9898, FILETRANSFER_PORT = 10153;
	public static final int TYPE_TEXT = 0, TYPE_PHOTO = 1, TYPE_VOICE = 2;
	public static final String Tag = "ClassLogin";
	public static final String TimeTag = "Timer";
	public static final String TransportTag = "Transport_Log";
	public static final String TXTRECORD_PROP_AVAILABLE = "available";
	public static final String TestTag = "Service_discovery_problem";
	
	
	// public static final String SERVICE_INSTANCE = "MsLabwifidemo";
	public static String SERVICE_INSTANCE = "ClassLogin";
	public static String SERVICE_REG_TYPE = "T_";
	public static double discovery_start_time, discovery_end_time_interval;
	public static boolean recevied = false;
	public static String trans_data_path = "";
	public long slot_time = 60;//15000
	public static int now_slot = 0;
	public int count_time = 0;
	public int device_role = -1;//0 = subtree root, 1 = next_child, 2 = prev_child

	public String Target_MAC = "";
	public String Change_Action_Target_MAC_0 = "";
	public String Change_Action_Target_MAC_1 = "";
	public String Change_Action_Target_MAC_2 = "";
	
	public static int slot_interval = 15;
	public int control_time = 1;
	public String subtree_serial = "-1";
	public String prev_serial = "-1";
	public String next_target_mac = "-1";
	public static boolean P2p_Connect_Switch = true;
	public static boolean transport_flag = false;
	public static boolean Change_Action_Switch = false;
	public static boolean Retransport_Switch = false;
	public static String Retransport_File_Name = "";
	
	public static int Retransport_Times = 0;
	public static int Third_slot = 0;
	
	String View_address = "";
	
	public WifiP2pDeviceList Result_Device_List = new WifiP2pDeviceList();
	
//	public RingNetwork network_process;
	public TimerAction network_process;
	
	
	private static Map<String, String> IPMap = new HashMap<String, String>();
	private static boolean isGOwner = false;
	private static boolean WifiP2pConnectState = false;
	private static WifiP2pInfo GInfo;
	private static ArrayList<WifiP2pDevice> devicelist;
//	ArrayList<Future<Map<String, Inet4Address>>> IParray = new ArrayList<Future<Map<String, Inet4Address>>>();

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;

	private final IntentFilter intentFilter = new IntentFilter();

	private int page_type = 0;
	public int[] EventTime = new int[3];
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WifiP2pDnsSdServiceRequest servicerequest;
	private WifiP2pServiceRequest servicequest_typeall;
	private WifiP2pManager manager;
	
	
	private WifiP2pDevice device;
	private boolean WifiP2p_Enable = false;
	private boolean retryChannel = false;
	private boolean IsServiceInRadius = false;
	private WifiP2pInfo info;
	private static Executor exec;
	private int FormNetTimer = 0;
	private MenuItem menuitem;
	
	
	protected void initialValue(){
		
		
		Calendar calendar = Calendar.getInstance();
		EventTime[0] = calendar.get(Calendar.YEAR);
		EventTime[1] = calendar.get(Calendar.MONTH) + 1;
		EventTime[2] = calendar.get(Calendar.DAY_OF_MONTH);
		
		// Set intentFilter
//		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
//		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
//		intentFilter
//				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
//		intentFilter
//				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
		// Setting Executor
//		exec = Executors.newFixedThreadPool(4);
//		exec = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		


	}

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainclass_tablayout);

		Toolbar toolBar = (Toolbar) findViewById(R.id.index_toolbar);
//		setSupportActionBar(toolBar);

//		final ActionBar actionbar = getSupportActionBar();

		// Add Tab
//
//		List<Fragment> fragments = new ArrayList<Fragment>();
//		fragments.add(new MainClassCalendarFragment());
//		fragments.add(new MainClassListFragment());
//
//		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//		if(viewPager != null){
//			viewPager.setAdapter(new MainClassPageAdapter(getSupportFragmentManager(), fragments, MainClassCalendarActivity.this));
//		}
//
//		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//		tabLayout.setupWithViewPager(viewPager);

//		Fragment CalendarFragment = new MainClassCalendarFragment();
//		Fragment ClassListFragment = new MainClassListFragment();

//		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		initialValue();



//		actionbar.addTab(actionbar
//				.newTab()
//				.setText(R.string.Main_Tab_Calendar)
//				.setTabListener(
//						new MainClassCalendarTabListener(CalendarFragment)));
//
//		actionbar.addTab(actionbar
//				.newTab()
//				.setText(R.string.Main_Tab_ClassList)
//				.setTabListener(
//						new MainClassCalendarTabListener(ClassListFragment)));
//
//		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
//		channel = manager.initialize(this, getMainLooper(), null);
		
//		network_process = new RingNetwork(this);
//		network_process.executeOnExecutor(exec);
		
//		network_process = new TimerAction(this);
//		network_process.executeOnExecutor(exec);
//		new HandshakeHandle(this).executeOnExecutor(exec);
//		new FileServerAsyncTask(this).executeOnExecutor(exec);
		
		
		//Ring Thread
//		Thread ring = new Thread(RunRingNetwork);
//		ring.start();

	}
	
	public static class TimerAction extends AsyncTask<Void, Void, Integer>{
		
		private Context context;
		private String MACIP;
		private long slot_time = 150;//15s
		private int now_slot = 1;
		private int count_time = 0;
		private int device_role = -1;//0 = subtree root, 1 = next_child, 2 = prev_child
		private String subtree_serial = "-1";
		private String prev_serial = "-1";
		private String next_target_mac = "-1";
		
		
		public TimerAction(Context context/*, String subtree_serial, String prev_serial, String next_target_mac*/){
			
			this.context = context;
//			
//			this.subtree_serial = subtree_serial;
//			this.prev_serial = prev_serial;
//			this.next_target_mac = next_target_mac;
		}
		
		public void change_role(int role){
			device_role = role;
		}
		
		public void set_network_parameter(int parameter_serial, String value){
			switch(parameter_serial){
			case 0:
				subtree_serial = value;
				Log.d(Tag, "0-set_network_parameter-subtree_serial:"+subtree_serial);
				
				break;
			case 1:
				next_target_mac = value;
				Log.d(Tag, "1-set_network_parameter-next_target_mac:"+next_target_mac);
				
				break;
			case 2:
				prev_serial = value;
				Log.d(Tag, "2-set_network_parameter-prev_serial:"+prev_serial);
				
				break;
			}
		}
		
		
		
		

		@Override
		protected Integer doInBackground(Void... params){
			// TODO Auto-generated method stub
			Timer action_time = new Timer();
			Date firstTime;
			Calendar calendar = Calendar.getInstance();
			int now_second = calendar.get(Calendar.SECOND);
			Log.d(TimeTag, "now_second:"+now_second);
			
			if((now_second % 30) > 15){
				now_slot = 1;
				
				
			}else{
				now_slot = 0;
			}
			
			if(now_second % 15 == 0){
				firstTime = calendar.getTime();
			}else{
				int interval = now_second % slot_interval;
				calendar.set(Calendar.SECOND, now_second + (slot_interval - interval));
				firstTime = calendar.getTime();
				
			}
			
			action_time.schedule(new ActionTask(), firstTime, slot_interval * 1000);
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Integer result){
			

			
		}
		
		@Override
		protected void onPreExecute(){
			// statusText.setText("Opening a server socket");
			
			
		}
		
		public class ActionTask extends TimerTask{

			@Override
			public void run(){
				// TODO Auto-generated method stub
				
				MainClassCalendarActivity UIFace = ((MainClassCalendarActivity) context);
				Calendar calendar = Calendar.getInstance();
				
				Log.d(TimeTag, "Time:"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
				
				Log.d(TimeTag, "subtree_serial:"+subtree_serial+" prev_serial:"+prev_serial+" next_target_mac:"+next_target_mac);
				
				if(device_role > -1){
					
					if(now_slot == 0){//slot 0 Action
						//Open Service
						if(device_role == 0){
							//subtree root action
//							UIFace.CloseService();
							Log.d(TimeTag, "FormSubtreeRoot");
							UIFace.FormSubtreeRoot();
							UIFace.openOptionService(subtree_serial, "subtree root");
							
						}else if(device_role > 0){
							//bg device action
							Log.d(TimeTag, "discovery~:"+prev_serial+"subtree root");
							UIFace.discoveryServiceAndReaction(prev_serial, 0);
							
						}
						
					}else{//slot 1 Action
						//Open Service
						
						if(device_role == 1){
							//bg_next
							Log.d(TimeTag, "discovery~nt:"+next_target_mac+"=>bg_prev");
							UIFace.discoveryServiceAndReaction(next_target_mac, 0);
							
							
						}
						if(device_role == 2){
							//bg_prev
//							UIFace.CloseService();
							Log.d(TimeTag, "openService~ps:"+prev_serial+"=>bg_prev");
							Log.d(TimeTag, "FormSubtreeRoot");
							UIFace.FormSubtreeRoot();
							UIFace.openOptionService(prev_serial, "bg_prev");
							
						}
						
						
					}
					
					try{
						Thread.sleep(14700);
						
						if(now_slot == 1){
							
							if(device_role == 2){
								UIFace.CloseService(prev_serial, "bg_prev");
								Log.d(TimeTag, "UIFace.CloseService:"+prev_serial+" bg_prev");
							}
						}
						UIFace.disconnect();
						Log.d(TimeTag, "UIFace.disconnect()");
						
						
						if(now_slot == 1){
							now_slot = 0;
							UIFace.SetSlot(now_slot);
							Log.d(TimeTag, "change to now_slot 0");
						}else{
							now_slot = 1;
							UIFace.SetSlot(now_slot);
							Log.d(TimeTag, "change to now_slot 1");
						}
//						P2p_Connect_Switch = true;
						
					}catch(InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d(TimeTag, "error:"+e);
					}
					
				}
				
				
				
			}
		}
	}
	
	
	public class PhotoTask extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			
			
			//"1_20150826_143942.jpg"
			
//			String s_uri = "/storage/sdcard0/PhotoNoteDir/1_20150817_153310.jpg", s_fname = "1_20150817_153310.jpg";
//			String s_uri = "/storage/sdcard0/PhotoNoteDir/TempPhoto.jpg", s_fname = "1_20150817_153310.jpg";
//			Uri uri = Uri.parse("file://" + s_uri);
			final File f = new File(Environment.getExternalStorageDirectory() + "/"
					+ PhotoNoteDir + "/" + "1_20150826_143942.jpg");
			
			Intent intent = new Intent();

			intent.putExtra("ExPageType", 1);
			intent.putExtra("NoteFilePath", f.getAbsolutePath());
			
			
			intent.setClass(MainClassCalendarActivity.this, NoteShowPhotoActivity.class);
			
//			Intent intent = new Intent();
//			String s_uri = "http://163.13.127.178/TempPhoto.jpg";
//			Uri uri = Uri.parse(s_uri);
//			
//			intent.setAction(android.content.Intent.ACTION_VIEW);
//			intent.setDataAndType(uri, "image/*");
			
			startActivity(intent);
			
		}
		
	}
	
	
	public class DiscoveryLoop extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			
			
			manager.discoverPeers(channel, new ActionListener(){

				@Override
				public void onSuccess(){
					// TODO Auto-generated method stub
					Log.d(TimeTag, "Loop discovery success");
				}

				@Override
				public void onFailure(int reason){
					// TODO Auto-generated method stub
					Log.d(TimeTag, "Loop discovery failed, reason:"+reason);
				}
			});
			
			
			
		}
		
	}
	
	
	
	

	@Override
	public void onResume(){
		super.onResume();
		// 對裝置註冊廣播接收器
		receiver = new WFD_BroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
		Log.d("", "register success");
	}

	@Override
	public void onPause(){
		super.onPause();
		// 解除註冊廣播接收器
		unregisterReceiver(receiver);
	}

	/*
	* ActionBarActivity function (need delete)
	* */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu){
////		getMenuInflater().inflate(R.menu.option, menu);
//		getMenuInflater().inflate(R.menu.main, menu);
//		menuitem = menu.findItem(R.id.action_insert_class);
//		menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//
//
////		menu.getItem(1).setEnabled(false);
////		menuitem.setEnabled(false);
//		/*if(WifiP2p_Enable){
////			menuitem.setVisible(true);
//			menuitem.setEnabled(true);
//		}else{
////			menuitem.setVisible(false);
//			menuitem.setEnabled(false);
//		}*/
//
//		return true;
//	}

	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent = new Intent();
		
		String service_Instance = "RingNetwork";
		String reg_Type = "subtree root";
		final String[] ListServiceName = {"RingNetwork_1", "RingNetwork_2", "RingNetwork_3"};
		Builder ListAlertDialog = new AlertDialog.Builder(this);
		final Builder RoleListDialog = new AlertDialog.Builder(this), Select_Target_MAC_0 = new AlertDialog.Builder(this), Select_Target_MAC_1 = new AlertDialog.Builder(this), Select_Target_MAC_2 = new AlertDialog.Builder(this);

		final String[] device_list = {"ASUS", "Sony-EQLT", "Sony-C8QJ", "NEXUS 7", "HTC_ONESV", "Samsung-white", "SIII", "MI"};
		final String[] device_MAC_list = {"7a:24:af:0a:ed:05", "96:ce:2c:7d:78:52", "96:ce:2c:7c:f6:72", "da:50:e6:8d:40:b1", "86:7a:88:0b:11:85", "c6:88:e5:a5:19:52", "5e:0a:5b:d6:5d:4a", "ae:f7:f3:ce:d5:1d"};
		
		switch (item.getItemId()){
		case R.id.action_insert:
			//原本的
			intent.putExtra("EventTime", EventTime);
			intent.setClass(MainClassCalendarActivity.this,
					MainClassCalendarInsertEventActivity.class);
			startActivity(intent);
			
//			
//			Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
//            intent2.setType("image/*");
//            startActivityForResult(intent2, CHOOSE_FILE_RESULT_CODE);

			break;

		case R.id.action_settings:
			
//			FormSubtreeRoot();
//			openOptionService("RingNetwork_1", "subtree root");
			
			
//			CloseService("RingNetwork_1", "bg_prev");
//			Log.d(Tag, "CloseService"+" RingNetwork_1"+" bg_prev");
			
//			Intent intent = new Intent();
			String s_uri = "http://163.13.127.178/TempPhoto.jpg";
			Uri uri = Uri.parse(s_uri);
			
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "image/*");
			
			startActivity(intent);
			
			
			break;

		case R.id.action_formnetwork:
			
			final Timer action_time = new Timer();
			final String[] view_time = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
			
			ListAlertDialog.setTitle("請選擇View Time:").setItems(view_time, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					Calendar calendar = Calendar.getInstance();
					int now_second = calendar.get(Calendar.SECOND);
					int now_minute = calendar.get(Calendar.MINUTE);
					
					
					calendar.set(Calendar.MINUTE, now_minute + 1);
					calendar.set(Calendar.SECOND, 0);
					
					Log.d(TimeTag, "set now_minute:"+now_minute);
					
					switch(which){
					case 0:
						calendar.set(Calendar.SECOND, 22);
						calendar.set(Calendar.MILLISECOND, 600);
						break;
					case 1:
						calendar.set(Calendar.SECOND, 8);
						calendar.set(Calendar.MILLISECOND, 0);
						break;
					case 2:
//						calendar.set(Calendar.SECOND, 1);
//						calendar.set(Calendar.MILLISECOND, 0);
						break;
					case 3:
						calendar.set(Calendar.SECOND, 1);
						calendar.set(Calendar.MILLISECOND, 0);
						break;
					case 4:
						calendar.set(Calendar.SECOND, 1);
						calendar.set(Calendar.MILLISECOND, 500);
						break;
					case 5:
						calendar.set(Calendar.SECOND, 7);
						calendar.set(Calendar.MILLISECOND, 700);
						break;
					case 6:
						calendar.set(Calendar.SECOND, 22);
						calendar.set(Calendar.MILLISECOND, 100);
						break;
					case 7:
						calendar.set(Calendar.SECOND, 22);
						calendar.set(Calendar.MILLISECOND, 700);
						break;
					case 8:
						calendar.set(Calendar.SECOND, 23);
						calendar.set(Calendar.MILLISECOND, 900);
						break;
					}
					
					Date firstTime = calendar.getTime();
					action_time.schedule(new PhotoTask(), firstTime, 60000);
					
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
			break;
		case R.id.action_openRingConnectionService:
			
//			Builder ListAlertDialog = new AlertDialog.Builder(this);
			
			ListAlertDialog.setTitle("請選擇開啟那個Service:").setItems(ListServiceName, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch(which){
					case 0:
						subtree_serial = "RingNetwork_1";
						network_process.set_network_parameter(0, subtree_serial);
						network_process.change_role(0);
						FormSubtreeRoot();
						openOptionService(subtree_serial, "subtree root");
						device_role = 0;
						break;
					case 1:
						subtree_serial = "RingNetwork_2";
						network_process.set_network_parameter(0, subtree_serial);
						network_process.change_role(0);
						FormSubtreeRoot();
						openOptionService(subtree_serial, "subtree root");
						device_role = 0;
						break;
					case 2:
						subtree_serial = "RingNetwork_3";
						network_process.set_network_parameter(0, subtree_serial);
						network_process.change_role(0);
						FormSubtreeRoot();
						openOptionService(subtree_serial, "subtree root");
						device_role = 0;
						break;
					}
					
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
			
			break;
			
			
		case R.id.action_discoveryRingService:
			
			final String[] role_list = {"bg_next", "bg_prev"};
			int select_role = -1;
			boolean role_choice = false;
			
			ListAlertDialog.setTitle("請選擇尋找那個Service:").setItems(ListServiceName, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch(which){
					case 0:
						Log.d(Tag, "S->"+0);
//						service_Instance = "RingNetwork";
//						reg_Type = "subtree root";
						prev_serial = "RingNetwork_1";
						next_target_mac = "RingNetwork_2";
						network_process.set_network_parameter(2, prev_serial);
						network_process.set_network_parameter(1, next_target_mac);
						network_process.set_network_parameter(0, prev_serial);
						discoveryServiceAndReaction(prev_serial, control_time);
						
						break;
					case 1:
						Log.d(Tag, "S->"+1);
//						service_Instance = "RingNetwork";
//						reg_Type = "subtree root";
						prev_serial = "RingNetwork_2";
						next_target_mac = "RingNetwork_3";
						network_process.set_network_parameter(2, prev_serial);
						network_process.set_network_parameter(1, next_target_mac);
						network_process.set_network_parameter(0, prev_serial);
						discoveryServiceAndReaction(prev_serial, control_time);
						break;
					case 2:
						Log.d(Tag, "S->"+2);
//						service_Instance = "RingNetwork";
//						reg_Type = "subtree root";
						prev_serial = "RingNetwork_3";
						next_target_mac = "RingNetwork_1";
						network_process.set_network_parameter(2, prev_serial);
						network_process.set_network_parameter(1, next_target_mac);
						network_process.set_network_parameter(0, prev_serial);
						discoveryServiceAndReaction(prev_serial, control_time);
						break;
					}
					
					control_time++;
					
					RoleListDialog.setTitle("請選擇bg_role:").setItems(role_list, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which){
							// TODO Auto-generated method stub
							switch(which){
							case 0://next
								Log.d(Tag, "S->bg_next");
								network_process.change_role(1);
								
								break;
							case 1://prev
								Log.d(Tag, "S->bg_prev");
								network_process.change_role(2);
								
								break;
							}
							
							
						}
						
					}).show();
					
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
			
			
			
			break;
		case R.id.action_Connect_toMAC:
			/*
			 * ASUS: 7a:24:af:0a:ed:05
			 * EQLT: 96:ce:2c:7d:78:52
			 * C8QJ: 96:ce:2c:7c:f6:72
			 * NEX7: da:50:e6:8d:40:b1
			 * HTCS: 86:7a:88:0b:11:85
			 * 
			 * */
			
			
			RoleListDialog.setTitle("請選擇連線裝置:").setItems(device_list, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which){
					// TODO Auto-generated method stub
					
					Target_MAC = device_MAC_list[which];
					final String device_name = device_list[which];
					
					manager.discoverPeers(channel, new ActionListener(){

						@Override
						public void onSuccess(){
							// TODO Auto-generated method stub
							Log.d(TestTag, "Discovery peer action success");
							Log.d(TestTag, "Set connect to:"+device_name+" that MAC address is "+Target_MAC);
						}

						@Override
						public void onFailure(int reason){
							// TODO Auto-generated method stub
							Log.d(TestTag, "Discovery peer action failed, reason_code:"+reason);
							
						}
						
					});
					
				}
				
			}).show();
			
			
			break;
			
		case R.id.action_open_one_service:
			
//			connectP2pFromMAC(Target_MAC);
//			DiscoveryService(control_time);
			
			FormSubtreeRoot();
			openOptionService("RingNetwork_1", "subtree root");
			
			
			
			
			
//			Log.d(TestTag, "Set control_time:"+control_time);
//			control_time++;
			
			
//			Thread ring = new Thread(RunRingNetwork);
//			ring.start();
			
			break;
			
		case R.id.action_broakcast_data:
			
			
			if(transport_flag){
				transport_flag = false;
				Log.d(TestTag, "transport_flag = false");
			}else{
				transport_flag = true;
				Log.d(TestTag, "transport_flag = true");
			}
			
//			String s_uri = "/storage/sdcard0/PhotoNoteDir/1_20150817_153310.jpg", s_fname = "1_20150817_153310.jpg";
//			Uri uri = Uri.parse("file://" + s_uri);
//			
//			Log.d(TestTag, "IPMap:"+IPMap);
//			
//			for(Object key : IPMap.keySet()){
//				ClassInformationPageActivity.sendFile((Context)this, 103, uri, 1, s_fname, IPMap.get(key));
//				Log.d(Tag, "server: transport to :" + IPMap.get(key));
//			}
			
//			for(WifiP2pDevice device : devicelist){
				
//			for(String ipmap : IPMap.){
				
//				ClassInformationPageActivity.sendFile((Context)this, 103, uri, 1, s_fname, IPMap.get(device.deviceAddress));
//				
//				Log.d(Tag, "server: transport to :" + IPMap.get(device.deviceAddress));
//			}
			
				
//			}
			
			
			break;
			
		case R.id.action_control:
			
			final String[] action_list = {"No discovery connect", "Disconnect", "Discovery peer", "Being group owner", "bg_next action", "bg_prev action", "Stop Discovery", "Test Transport", "T_A"};
			RoleListDialog.setTitle("選擇WiFi P2P動作:").setItems(action_list, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which){
					// TODO Auto-generated method stub
					switch(which){
					case 0:
						
						
						break;
					case 1:
						
						disconnect();
						
						break;
					case 2:
						
						manager.discoverPeers(channel, new ActionListener(){
							
							@Override
							public void onSuccess(){
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onFailure(int reason){
								// TODO Auto-generated method stub
								
							}
						});
						
						
						
						
						break;
					case 3://Being group owner
						
						FromGroup();
						
						
						break;
					case 4://bg_next action
						
						/* ASUS: 7a:24:af:0a:ed:05
						 * EQLT: 96:ce:2c:7d:78:52
						 * C8QJ: 96:ce:2c:7c:f6:72
						 * NEX7: da:50:e6:8d:40:b1
						 * HTCS: 86:7a:88:0b:11:85
						 * Sams: c6:88:e5:a5:19:52
						 * 小米:  ae:f7:f3:ce:d5:1d
						 * */
						
						Select_Target_MAC_0.setTitle("選擇連線裝置0:").setItems(device_list, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which){
								// TODO Auto-generated method stub
								Change_Action_Target_MAC_0 = device_MAC_list[which];
								
								Select_Target_MAC_1.setTitle("選擇連線裝置1:").setItems(device_list, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which){
										// TODO Auto-generated method stub
										Change_Action_Target_MAC_1 = device_MAC_list[which];
										
										Timer action_time;
										bg_next_action connect;
										Date firstTime;
										Calendar calendar;
										int now_second;
										
										action_time = new Timer();
										connect = new bg_next_action();
										calendar = Calendar.getInstance();
										now_second = calendar.get(Calendar.SECOND);
										
										if((now_second % 30) > 15){
											//Next Slot = 0
											Change_Action_Switch = false;
										}else{
											//Next Slot = 1
											Change_Action_Switch = true;
										}
										
										if(now_second % 15 == 0){
											firstTime = calendar.getTime();
											
										}else{
											int interval = now_second % slot_interval;
											calendar.set(Calendar.SECOND, now_second + (slot_interval - interval));
											firstTime = calendar.getTime();
										}
										
//										action_time.schedule(new slot_change_action(), slotTime, slot_interval * 1000);
										action_time.schedule(new Loop_Discovery_Timer_bg_next(), firstTime, slot_interval * 1000);
										action_time.schedule(connect, firstTime, slot_interval * 1000);
										
										Log.d(TestTag, "Change_Action_Target_MAC_0"+Change_Action_Target_MAC_0+"Change_Action_Target_MAC_1"+Change_Action_Target_MAC_1);
									}
								}).show();
							}
							
						}).show();
						
//						Change_Action_Target_MAC_0 = "7a:24:af:0a:ed:05";
//						Change_Action_Target_MAC_1 = "da:50:e6:8d:40:b1";
						
//						action_time = new Timer();
//						connect = new Connect_Process();
//						calendar = Calendar.getInstance();
//						now_second = calendar.get(Calendar.SECOND);
//						
//						if((now_second % 30) > 15){
//							//Slot = 1
//							Change_Action_Switch = true;
//						}else{
//							//Slot = 0
//							Change_Action_Switch = false;
//						}
//						
//						if(now_second % 15 == 0){
//							firstTime = calendar.getTime();
//							
//						}else{
//							int interval = now_second % slot_interval;
//							calendar.set(Calendar.SECOND, now_second + (slot_interval - interval));
//							firstTime = calendar.getTime();
//							now_second = calendar.get(Calendar.SECOND);
//							
//						}
//						calendar.set(Calendar.SECOND, now_second + slot_interval - 1);
//						slotTime = calendar.getTime();
//						
//						action_time.schedule(new slot_change_action(), slotTime, slot_interval * 1000);
//						action_time.schedule(new Loop_Discovery_Timer(), firstTime, slot_interval * 1000);
//						action_time.schedule(connect, firstTime, slot_interval * 1000);
//						
						
						
						break;
						
					case 5://bg_prev action
						
						
						Select_Target_MAC_0.setTitle("選擇Group owner:").setItems(device_list, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which){
								// TODO Auto-generated method stub
								Change_Action_Target_MAC_0 = device_MAC_list[which];
								
								Timer action_time;
								Date firstTime, secondTime;
								Calendar calendar;
								int now_second, now_minute;
								
								action_time = new Timer();
								calendar = Calendar.getInstance();
								now_second = calendar.get(Calendar.SECOND);
								
								if((now_second % 30) > 15){
									//Next Slot = 0
									Change_Action_Switch = false;
								}else{
									//Next Slot = 1
									Change_Action_Switch = true;
								}
								
								if(now_second % 30 == 0){
									firstTime = calendar.getTime();
								}else{
									int interval = now_second % (slot_interval * 2);
									calendar.set(Calendar.SECOND, now_second + ((slot_interval * 2) - interval));
									firstTime = calendar.getTime();
								}
								now_second = calendar.get(Calendar.SECOND);
								now_second += 14;
								calendar.set(Calendar.SECOND, now_second);
								secondTime = calendar.getTime();
								
//								action_time.schedule(new slot_change_action(), slotTime, slot_interval * 1000);
								
								action_time.schedule(new Loop_Discovery_Timer_bg_prev(), firstTime, slot_interval * 1000 * 2);
								action_time.schedule(new Loop_Discovery_Timer_bg_prev(), secondTime, slot_interval * 1000 * 2);
								
								action_time.schedule(new bg_prev_action(), firstTime, slot_interval * 1000);
								
							}
							
						}).show();
						
						
//						Change_Action_Target_MAC_0 = "7a:24:af:0a:ed:05";//********Group owner
						
//						action_time = new Timer();
//						connect = new Connect_Process();
//						calendar = Calendar.getInstance();
//						now_second = calendar.get(Calendar.SECOND);
//						
//						if((now_second % 30) > 15){
//							//Slot = 1
//							Change_Action_Switch = true;
//						}else{
//							//Slot = 0
//							Change_Action_Switch = false;
//						}
//						
//						if(now_second % 15 == 0){
//							firstTime = calendar.getTime();
//						}else{
//							int interval = now_second % slot_interval;
//							calendar.set(Calendar.SECOND, now_second + (slot_interval - interval));
//							firstTime = calendar.getTime();
//							now_second = calendar.get(Calendar.SECOND);
//							
//							
//						}
//						calendar.set(Calendar.SECOND, now_second + slot_interval - 1);
//						slotTime = calendar.getTime();
//						
//						action_time.schedule(new slot_change_action(), slotTime, slot_interval * 1000);
//						action_time.schedule(new bg_prev_action(), firstTime, slot_interval * 1000);
						
						
						
						break;
					case 6://Stop Discovery
						
						manager.stopPeerDiscovery(channel, new ActionListener(){

							@Override
							public void onSuccess(){
								// TODO Auto-generated method stub
								Log.d(TestTag, "stopPeerDiscovery Stop Success");
							}

							@Override
							public void onFailure(int reason){
								// TODO Auto-generated method stub
								Log.d(TestTag, "stopPeerDiscovery Stop failed, reason:"+ErrorCode(reason)+"("+reason+")");
							}
							
						});
						
						
						break;
					case 7://Test Transport
						
						Loop_Send_Photo("1_20150826_143942.jpg");
						
						break;
						
					case 8://T_A
						/* ASUS: 7a:24:af:0a:ed:05
						 * EQLT: 96:ce:2c:7d:78:52
						 * C8QJ: 96:ce:2c:7c:f6:72
						 * NEX7: da:50:e6:8d:40:b1
						 * HTCS: 86:7a:88:0b:11:85
						 * Sams: c6:88:e5:a5:19:52
						 * 小米:  ae:f7:f3:ce:d5:1d
						 * */
						
						Select_Target_MAC_0.setTitle("選擇連線裝置_1:").setItems(device_list, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which){
								// TODO Auto-generated method stub
								Change_Action_Target_MAC_0 = device_MAC_list[which];
								
								Select_Target_MAC_1.setTitle("選擇連線裝置_2:").setItems(device_list, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which){
										// TODO Auto-generated method stub
										Change_Action_Target_MAC_1 = device_MAC_list[which];
										
										Select_Target_MAC_2.setTitle("選擇連線裝置_3:").setItems(device_list, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which){
												// TODO Auto-generated method stub
												Change_Action_Target_MAC_2 = device_MAC_list[which];
												
												Timer action_time;
												Date firstTime;
												Calendar calendar;
												int now_minute;
												
												action_time = new Timer();
												third_action connect = new third_action();
												
												calendar = Calendar.getInstance();
												now_minute = calendar.get(Calendar.MINUTE);
												
												calendar.set(Calendar.MINUTE, now_minute + 1);
												calendar.set(Calendar.SECOND, 0);
												firstTime = calendar.getTime();
												
												
//												action_time.schedule(new slot_change_action(), slotTime, slot_interval * 1000);
												action_time.schedule(new Loop_Discovery_Timer_bg_next(), firstTime, slot_interval * 1000);
												action_time.schedule(connect, firstTime, slot_interval * 1000);
												
												Log.d(TestTag, "Change_Action_Target_MAC_0:"+Change_Action_Target_MAC_0+" Change_Action_Target_MAC_1:"+Change_Action_Target_MAC_1+" Change_Action_Target_MAC_2:"+Change_Action_Target_MAC_2);
												
												
												
											}
										}).show();
										
										
										
									}
								}).show();
							}
							
						}).show();
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						break;
					}
					
					
					
					if(which == 0){
						RoleListDialog.setTitle("選擇連線裝置:").setItems(device_list, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which){
								// TODO Auto-generated method stub
								
								Target_MAC = device_MAC_list[which];
								
								WifiP2pConfig config = new WifiP2pConfig();
								config.deviceAddress = Target_MAC;
								config.wps.setup = WpsInfo.PBC;
								
								manager.connect(channel, config, new ActionListener(){

									@Override
									public void onSuccess(){
										// TODO Auto-generated method stub
										Log.d(TestTag, "No discovery connect Success!(Happy!)");
									}

									@Override
									public void onFailure(int reason){
										// TODO Auto-generated method stub
										Log.d(TestTag, "No discovery connect failed.....!(So...sad...), reason is "+ErrorCode(reason)+"("+reason+")");
									}
									
								});
								
							}
							
						}).show();
						
					}
					
				}
				
			}).show();
			
			
			break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	
	public class Loop_Discovery_Timer_bg_next extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			Log.d(TestTag, "Loop_Discovery_Timer");
			
			if(WifiP2pConnectState){
				
				Log.d(TestTag, "First, Disconnect!");
				
				manager.removeGroup(channel, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						Log.d(Tag, "Disconnect success");
						WifiP2pConnectState = false;
						Log.d(TestTag, "Start Discovery_1!");
						
						manager.discoverPeers(channel, new ActionListener(){
							
							@Override
							public void onSuccess(){
								// TODO Auto-generated method stub
								Log.d(TestTag, "P2p_Connect_Switch:discovey success");
							}
							
							@Override
							public void onFailure(int reason){
								// TODO Auto-generated method stub
								Log.d(TestTag, "P2p_Connect_Switch:discovey failed, reason:"+reason);
							}
						});
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(Tag, "Disconnect failed. Reason :" + ErrorCode(reason)+"("+reason+")");
					}});
				
			}else{
				
				Log.d(TestTag, "Start Discovery_2!");
				
				manager.discoverPeers(channel, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						Log.d(TestTag, "P2p_Connect_Switch:discovey success");
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(TestTag, "P2p_Connect_Switch:discovey failed, reason:"+reason);
					}
				});
			}
			
		}
	}
	//******************************************************************************************************************//
	//S_Code
	
	public class third_action extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			
			
			//Clear PeerList
			Result_Device_List = new WifiP2pDeviceList();
			
			long process_start_time = System.currentTimeMillis();
			
			WifiP2pConfig config = new WifiP2pConfig();
			boolean device_select = false;
			
			Log.d("Third_Log", "Connect Process Start!");
			
			while(!device_select){
				
				for(WifiP2pDevice device : Result_Device_List.getDeviceList()){
					if(device.deviceAddress.equals(Change_Action_Target_MAC_0) && Third_slot == 0){//slot 0
						Log.d("Third_Log", "Search to Target_MAC_0:"+Change_Action_Target_MAC_0);
						
						config.deviceAddress = Change_Action_Target_MAC_0;
						View_address = Change_Action_Target_MAC_0;
						device_select = true;
						
					}else if(device.deviceAddress.equals(Change_Action_Target_MAC_1) && Third_slot == 1){//slot 1
						Log.d("Third_Log", "Search to Target_MAC_1:"+Change_Action_Target_MAC_1);
						
						config.deviceAddress = Change_Action_Target_MAC_1;
						View_address = Change_Action_Target_MAC_1;
						
						device_select = true;
					}else if(device.deviceAddress.equals(Change_Action_Target_MAC_2) && Third_slot == 2){
						
						Log.d("Third_Log", "Search to Target_MAC_2:"+Change_Action_Target_MAC_2);
						
						config.deviceAddress = Change_Action_Target_MAC_2;
						View_address = Change_Action_Target_MAC_2;
						
						device_select = true;
					}else if(Third_slot == 3){
						device_select = true;
						break;
					}
				}
//				Log.d("Waiting_Log", "Interval time:"+(System.currentTimeMillis() - process_start_time));
				//reject section
				if(System.currentTimeMillis() - process_start_time > 8000){
					Log.d("Third_Log", "Interval time:"+(System.currentTimeMillis() - process_start_time));
					break;
				}
			}
			
			if(device_select && Third_slot < 3){
				Log.d("Third_Log", "Selected Targer Device, then start connecting!");
				manager.connect(channel, config, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
//						P2p_Connect_Switch = false;
						Log.d("Third_Log", "Connect to "+View_address+" Success");
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d("Third_Log", "Connect failed, reason_code:"+reason);
					}
					
				});
			}
			
			
			if(Third_slot < 3){
				Third_slot++;
			}else{
				Third_slot = 0;
			}
			
			
			long Retransport_start_time = System.currentTimeMillis();
			
			if(device_select && Retransport_Times > 0){
				while(!getP2pState()){
					//reject section
					if(System.currentTimeMillis() - Retransport_start_time > 6000){
						Log.d("Third_Log", "Interval time:"+(System.currentTimeMillis() - Retransport_start_time));
						break;
					}
				}
				
				Loop_Send_Photo(Retransport_File_Name);
				Retransport_Times--;
				Log.d("Third_Log", "Retransport_File_Name:"+Retransport_File_Name);
			}
			
		}
		
		
	}
	
	
	
	//******************************************************************************************************************//
	private static boolean Discovery_result = false;
	
	public class bg_next_action extends TimerTask{
		
		@Override
		public void run(){
			// TODO Auto-generated method stub
			//Clear PeerList
			Result_Device_List = new WifiP2pDeviceList();
			
			long process_start_time = System.currentTimeMillis();
			
			WifiP2pConfig config = new WifiP2pConfig();
			boolean device_select = false;
			
			
			Log.d(TestTag, "Connect Process Start!");
			
			while(!device_select/* && Discovery_result*/){
				
				for(WifiP2pDevice device : Result_Device_List.getDeviceList()){
					if(device.deviceAddress.equals(Change_Action_Target_MAC_0) && !Change_Action_Switch){//slot 0
						Log.d(TestTag, "Search to Target_MAC_0:"+Change_Action_Target_MAC_0);
						
						config.deviceAddress = Change_Action_Target_MAC_0;
						View_address = Change_Action_Target_MAC_0;
						device_select = true;
						
					}else if(device.deviceAddress.equals(Change_Action_Target_MAC_1) && Change_Action_Switch){//slot 1
						Log.d(TestTag, "Search to Target_MAC_1:"+Change_Action_Target_MAC_1);
						
						config.deviceAddress = Change_Action_Target_MAC_1;
						View_address = Change_Action_Target_MAC_1;
						
						device_select = true;
					}
				}
//				Log.d("Waiting_Log", "Interval time:"+(System.currentTimeMillis() - process_start_time));
				//reject section
				if(System.currentTimeMillis() - process_start_time > 8000){
					Log.d("Time_test_log", "Interval time:"+(System.currentTimeMillis() - process_start_time));
					break;
				}
			}
			
			if(device_select){
				Log.d(TestTag, "Selected Targer Device, then start connecting!");
				manager.connect(channel, config, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
//						P2p_Connect_Switch = false;
						Log.d(TestTag, "Connect to "+View_address+" Success");
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(TestTag, "Connect failed, reason_code:"+reason);
					}
					
				});
//				device_select = false;
			}
			
			
			if(Change_Action_Switch){
				Change_Action_Switch = false;
			}else{
				Change_Action_Switch = true;
			}
			
			long Retransport_start_time = System.currentTimeMillis();
			
			if(device_select /*&& Discovery_result*/ && Retransport_Switch){
				while(!getP2pState()){
					//reject section
					if(System.currentTimeMillis() - Retransport_start_time > 6000){
						Log.d("Time_test_log", "Interval time:"+(System.currentTimeMillis() - Retransport_start_time));
						break;
					}
				}
				
				Loop_Send_Photo(Retransport_File_Name);
				Retransport_Switch = false;
				Log.d(TransportTag, "Retransport_File_Name_0:"+Retransport_File_Name);
			}
		}
	}
	
	public static boolean bg_prev_slot_switch = false;
	public static boolean disconnect_result = false;
	
	public class Loop_Discovery_Timer_bg_prev extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			Log.d(TestTag, "Loop_Discovery_Timer_bg_prev");
			
			disconnect_result = false;
			
			if(WifiP2pConnectState){
				
				Log.d(TestTag, "First, Disconnect!");
				
				manager.removeGroup(channel, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						Log.d(Tag, "LDP_Disconnect success");
						WifiP2pConnectState = false;
						disconnect_result = true;
						
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(Tag, "LDP_Disconnect failed. Reason :" + ErrorCode(reason)+"("+reason+")");
						disconnect_result = true;
					}
				});
				
			}
			
			while(!disconnect_result && WifiP2pConnectState);
			
			if(!bg_prev_slot_switch){//slot 0
				
				Log.d(TestTag, "Start Discovery_bg_prev_3!");
				
				manager.discoverPeers(channel, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						Log.d(TestTag, "P2p_Connect_Switch:discovey success");
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(TestTag, "P2p_Connect_Switch:discovey failed, reason:"+reason);
					}
				});
				
				bg_prev_slot_switch = true;
				
			}else{//slot 1
				
				manager.createGroup(channel, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						Log.d(TestTag, "bg_prev createGroup success");
						
//						Log.d(TestTag, "Start Discovery_bg_prev_4!");
//						
//						manager.discoverPeers(channel, new ActionListener(){
//							
//							@Override
//							public void onSuccess(){
//								// TODO Auto-generated method stub
//								Log.d(TestTag, "P2p_Connect_Switch:discovey success");
//							}
//							
//							@Override
//							public void onFailure(int reason){
//								// TODO Auto-generated method stub
//								Log.d(TestTag, "P2p_Connect_Switch:discovey failed, reason:"+reason);
//							}
//						});
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(TestTag, "bg_prev createGroup failed, reason:"+reason);
					}
				});
				
				
				
				bg_prev_slot_switch = false;
				
			}
				
			
			
		}
	}
	
	public class bg_prev_action extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			
			Result_Device_List = new WifiP2pDeviceList();
			
			Log.d(TestTag, "bg_prev_action");
			
			if(!Change_Action_Switch){//slot = 0
				
				long process_start_time = System.currentTimeMillis();
				WifiP2pConfig config = new WifiP2pConfig();
				boolean device_select = false;
				
				Log.d(TestTag, "Connect Process Start! when device_select:"+device_select+" Discovery_result:"+Discovery_result);
				
				while(!device_select/* && Discovery_result*/){
//					Log.d("DeviceList", "Check Process !");
					for(WifiP2pDevice device : Result_Device_List.getDeviceList()){
//						Log.d("DeviceList", "device:"+device.deviceName+" MAC:"+device.deviceAddress);
						if(device.deviceAddress.equals(Change_Action_Target_MAC_0) ){
							Log.d(TestTag, "Search to Target_MAC:"+Change_Action_Target_MAC_0);
							
							config.deviceAddress = Change_Action_Target_MAC_0;
							View_address = Change_Action_Target_MAC_0;
							device_select = true;
							
						}
					}
					
					//reject section
					if(System.currentTimeMillis() - process_start_time > 8000){
						Log.d("Time_test_log", "Interval time:"+(System.currentTimeMillis() - process_start_time));
						break;
					}
					
				}
				
				if(device_select){
					Log.d(TestTag, "Selected Targer Device, then start connecting!");
					manager.connect(channel, config, new ActionListener(){
						
						@Override
						public void onSuccess(){
							// TODO Auto-generated method stub
//							P2p_Connect_Switch = false;
							Log.d(TestTag, "Connect to "+View_address+" Success");
						}
						
						@Override
						public void onFailure(int reason){
							// TODO Auto-generated method stub
							Log.d(TestTag, "Connect failed, reason_code:"+reason);
						}
						
					});
//					device_select = false;
				}
				
				
				long Retransport_start_time = System.currentTimeMillis();
				
				if(device_select /*&& Discovery_result */&& Retransport_Switch){
					while(!getP2pState()){
						//reject section
						if(System.currentTimeMillis() - Retransport_start_time > 6000){
							Log.d("Time_test_log", "Interval time:"+(System.currentTimeMillis() - Retransport_start_time));
							break;
						}
					}
					
					Loop_Send_Photo(Retransport_File_Name);
					Retransport_Switch = false;
					Log.d(TransportTag, "Retransport_File_Name_1:"+Retransport_File_Name);
				}
				
			}else{//slot = 1
				
				//Action move to Loop_Discovery_Timer_bg_prev
				
				long Retransport_start_time = System.currentTimeMillis();
				if(Retransport_Switch){
					
					while(!getP2pState()){
						//reject section
						if(System.currentTimeMillis() - Retransport_start_time > 9000){
							Log.d("Time_test_log", "Interval time:"+(System.currentTimeMillis() - Retransport_start_time));
							break;
						}
					}
					
					try{
						Thread.sleep(5000);
						Loop_Send_Photo(Retransport_File_Name);
						Retransport_Switch = false;
						Log.d(TransportTag, "Retransport_File_Name_2:"+Retransport_File_Name);
					}catch(InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				
				
			}
			
			if(Change_Action_Switch){
				Change_Action_Switch = false;
			}else{
				Change_Action_Switch = true;
			}
		}
	}
	
	public class slot_change_action extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			if(Change_Action_Switch){
				Change_Action_Switch = false;
			}else{
				Change_Action_Switch = true;
			}
		}
		
	}
	
	
	public void Loop_Send_Photo(String s_fname){
		
		Uri uri = null;
		File Temp = new File(Environment.getExternalStorageDirectory() + "/"
				+ PhotoNoteDir + "/" + s_fname);
		
		try{
			uri = Uri.parse(android.provider.MediaStore.Images.Media
					.insertImage(getContentResolver(), Temp.getAbsolutePath(),
							null, null));
		}catch(FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(uri != null){
			ClassInformationPageActivity.checkAndSend((Context)MainClassCalendarActivity.this, 103, uri, s_fname, 1);
			Log.d("Sending Time", "Sending Start Time:"+System.currentTimeMillis());
		}else{
			Log.e("ShareNote_TypePhoto", "uri error, can't be null!");
		}
		
	}
	
	
	
	
	
	Runnable AutoFormWiFiP2pNetwork_runnable = new Runnable(){
		
		@Override
		public void run(){
			Random r = new Random();
			int base = 5000;
			
			
			int WaitTime = (int)(r.nextInt(base)+5000);
			
//			DiscoveryService();
			try{
				Log.d(Tag, "State:" + WifiP2pConnectState);
				while(FormNetTimer < WaitTime && !WifiP2pConnectState){
					Thread.sleep(1);
					FormNetTimer++;
					if(FormNetTimer % 1000 == 0){
						Log.d("AutoFormNetwork", "WaitTime:"+WaitTime+" FormNetTimer:"+FormNetTimer+" WifiP2pConnectState:"+WifiP2pConnectState+
								"  IsServiceInRadius:"+IsServiceInRadius);
					}
				}
				
			}catch(InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("AutoFormNetwork", " WifiP2pConnectState:"+WifiP2pConnectState+
					"  IsServiceInRadius:"+IsServiceInRadius);
			
			
			if(IsServiceInRadius){
				while(!WifiP2pConnectState){
					
//					if(!WifiP2pConnectState)DiscoveryService();
					
					try{
						//3s Reconnect
						Thread.sleep(3000);
					}catch(InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
								
			}else if(!WifiP2pConnectState){
				FromGroupAndOpenSerivce();
				
				try{
					//3s Reconnect
					Thread.sleep(1000);
				}catch(InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while(isGOwner){
					
					OnlyDiscoveryService();
					
					try{
						//3s Reconnect
						Thread.sleep(8000);
					}catch(InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
			}
			
		}
	};
	
	public static void SendHandshake(Context context, String MAC, WifiP2pInfo Info){
		Log.d(Tag, "Handshake to:"+MAC);
		// Handshake
		Intent HandshakeService = new Intent(context, SendHandshake.class);
		HandshakeService.setAction(SendHandshake.ACTION_SEND_HANDSHAKE);
		// 要傳送的目的地Address
		Log.d(Tag, "Info.groupOwnerAddress:"+Info.groupOwnerAddress);
		HandshakeService.putExtra(SendHandshake.EXTRAS_GROUP_OWNER_ADDRESS,
				Info.groupOwnerAddress.getHostAddress());
		// Handshake所使用的PORT
		HandshakeService.putExtra(SendHandshake.EXTRAS_GROUP_OWNER_PORT,
				HANDSHAKE_PORT);
		HandshakeService.putExtra(SendHandshake.EXTRAS_DEVICE_MAC, MAC);
		if(context.startService(HandshakeService) != null){
			Log.d(Tag, "HandshakeService Open Success");
		}else{
			Log.d(Tag, "HandshakeService Open Failed");
		}
	}

	@Override
	public void OnDateSeletd(int year, int month, int DayOfMonth){
		EventTime[0] = year;
		EventTime[1] = month;
		EventTime[2] = DayOfMonth;
		Log.i("TimeTag_2", "Y:" + year + "M:" + month + "DoM:" + DayOfMonth);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){

		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		String dstAddress = null;
		for(int i = 0; i < devicelist.size(); i++){
			Log.d(Tag, "ClientList(" + i + "):"
					+ devicelist.get(i).deviceAddress);
			dstAddress = devicelist.get(i).deviceAddress;
			// Log.d(Tag, "ClientAddress("+i+"):"+devicelist.get(i).);
		}
		
		Uri uri = data.getData();
		Log.d(Tag, "Intent----------- " + uri);
		
		Log.d(Tag, "TEST URI----------- " + Uri.parse(uri.toString()));
		
		Intent serviceIntent = new Intent(MainClassCalendarActivity.this,
				FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
				uri.toString());
//		 serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
//		 info.groupOwnerAddress.getHostAddress());
		// serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
		// 8988);
		if(isGOwner && dstAddress != null){
			serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, IPMap.get(dstAddress));			
		}else{
			serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, GInfo.groupOwnerAddress.getHostAddress());
		}
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,	10153);
		MainClassCalendarActivity.this.startService(serviceIntent);
		
		
	}

	/*******************************/
	public void FromGroupAndOpenSerivce(){
		manager.createGroup(channel, new ActionListener(){

			@Override
			public void onFailure(int reason){
				
			}

			@Override
			public void onSuccess(){
				isGOwner = true;
			}

		});
		OpenService();
	}
	
	public void FromGroup(){
		manager.createGroup(channel, new ActionListener(){
			
			@Override
			public void onSuccess(){
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int reason){
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	
	boolean result = false;
	public boolean FormSubtreeRoot(){
		manager.createGroup(channel, new ActionListener(){

			@Override
			public void onFailure(int reason){
				result = false;
			}

			@Override
			public void onSuccess(){
				device_role = 0;
				result = true;
			}

		});
		
		return result;
	}
	
	
	public void WifiP2pEnable(boolean WifiP2p_Enable){
		this.WifiP2p_Enable = WifiP2p_Enable;
	}

	public void ClearPeerData(){

	}

	public void UpdateThisDevice(WifiP2pDevice device){
		this.device = device;
	}

	public void OpenService(){
		Map<String, String> record = new HashMap<String, String>();
		record.put(TXTRECORD_PROP_AVAILABLE, "visible");
		
		//SERVICE_REG_TYPE = SERVICE_REG_TYPE + Time;

		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		
		manager.addLocalService(channel, service, new ActionListener(){

			@Override
			public void onFailure(int reason){
				Log.e(Tag, "addlocalService_fail:" + reason);
			}

			@Override
			public void onSuccess(){
				Log.d(Tag, "addlocalService");
			}

		});
		Log.d(Tag, "Service.toString():" + service.toString());

		DiscoveryService(-1);
	}
	
	public void openOptionService(String service_Instance, String service_Reg_Type){
		Map<String, String> record = new HashMap<String, String>();
		record.put(TXTRECORD_PROP_AVAILABLE, "visible");
		
		SERVICE_INSTANCE = service_Instance;
		SERVICE_REG_TYPE = service_Reg_Type;
		
		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		
		
		manager.addLocalService(channel, service, new ActionListener(){

			@Override
			public void onFailure(int reason){
				Log.e(Tag, "addlocalService_fail:" + reason);
			}

			@Override
			public void onSuccess(){
				Log.d(Tag, "addlocalService INS:"+SERVICE_INSTANCE+" REG:"+SERVICE_REG_TYPE+" Success");
			}

		});
		Log.d(Tag, "Service.toString():" + service.toString());

		DiscoveryService(-2);
		
	}
	

	public void CloseService(){
		Map<String, String> record = new HashMap<String, String>();
		record.put(TXTRECORD_PROP_AVAILABLE, "visible");

		if(servicerequest != null)
			manager.removeServiceRequest(channel, servicerequest,
					new ActionListener(){

						@Override
						public void onSuccess(){
							Log.d(Tag, "removeServiceRequest:Success");
						}

						@Override
						public void onFailure(int reason){
							Log.e(Tag, "addlocalService_fail:" + reason);
						}
					});
		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		manager.removeLocalService(channel, service, new ActionListener(){
			
			@Override
			public void onSuccess(){
				Log.d(Tag, "removeLocalService:Success");
			}

			@Override
			public void onFailure(int reason){
				Log.e(Tag, "addlocalService_fail:" + reason);
			}

		});
	}
	
	
	public void CloseService(String service_Instance, String reg_Type){
		Map<String, String> record = new HashMap<String, String>();
		record.put(TXTRECORD_PROP_AVAILABLE, "visible");

		if(servicerequest != null)
			manager.removeServiceRequest(channel, servicerequest,
					new ActionListener(){

						@Override
						public void onSuccess(){
							Log.d(Tag, "removeServiceRequest:Success");
						}

						@Override
						public void onFailure(int reason){
							Log.e(Tag, "addlocalService_fail:" + reason);
						}
					});
		SERVICE_INSTANCE = service_Instance;
		SERVICE_REG_TYPE = reg_Type;
		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		manager.removeLocalService(channel, service, new ActionListener(){
			
			@Override
			public void onSuccess(){
				Log.d(Tag, "removeLocalService:Success");
			}

			@Override
			public void onFailure(int reason){
				Log.e(Tag, "addlocalService_fail:" + reason);
			}

		});
	}

	public void DiscoveryService(final int control_time){

		manager.setDnsSdResponseListeners(channel,
				new DnsSdServiceResponseListener(){

					public void onDnsSdServiceAvailable(String instanceName,
							String registrationType, WifiP2pDevice srcDevice){
						
						Log.d(Tag, "In DiscoveryService()--");
						Log.d(Tag,
								"setDnsSdResponseListeners_onDnsSdServiceAvailable");
						Log.d(Tag, "instanceName:" + instanceName
								+ "registrationType:" + registrationType);
						// 探測到的服務，用裡面的參數去判斷是否是我們要連線的服務(Service)
						
						Log.d(TestTag, "Type_normal=>This is "+control_time+" times result~action");
						Log.d(TestTag, "instanceName:" + instanceName
								+ "registrationType:" + registrationType);
						
						
						long now = System.currentTimeMillis();
						Log.d(TestTag, "Now:"+now);
						
						if(instanceName.equalsIgnoreCase(SERVICE_INSTANCE) && !isGOwner){
	
							WiFiP2pService service = new WiFiP2pService();
							service.device = srcDevice;
							service.instanceName = instanceName;
							service.serviceRegistrationType = registrationType;
							IsServiceInRadius = true;
							Log.d(Tag, "isGO:" + isGOwner);
							if(!isGOwner && !WifiP2pConnectState){
//								connectP2p(service);
								
							}
	//						

						}

					}
				}, new DnsSdTxtRecordListener(){

					public void onDnsSdTxtRecordAvailable(String arg0,
							Map<String, String> arg1, WifiP2pDevice arg2){
						
						Log.d(Tag,
								"setDnsSdResponseListeners_onDnsSdTxtRecordAvailable");
					}
				});

		servicerequest = WifiP2pDnsSdServiceRequest.newInstance();
		manager.addServiceRequest(channel, servicerequest,
				new ActionListener(){
			
					@Override
					public void onFailure(int reason){
						
						Log.d(Tag, "addServiceRequest_Fail:"+reason);
					}

					@Override
					public void onSuccess(){
						
						Log.d(Tag, "addServiceRequest_Success");
					}

		});
		
		
		
		
		
		manager.discoverServices(channel, new ActionListener(){

			@Override
			public void onFailure(int reason){
				Log.d(Tag, "discoverServices_Fail"+reason);
			}

			@Override
			public void onSuccess(){
				Log.d(Tag, "discoverServices_Success");
			}

		});

	}
	
	
	
	public void discoveryOptionService(final String service_Instance, final String reg_Type, final int action_slot, final int control_time){

		
		manager.setDnsSdResponseListeners(channel,
				new DnsSdServiceResponseListener(){

					public void onDnsSdServiceAvailable(String instanceName,
							String registrationType, WifiP2pDevice srcDevice){
						
						Log.d(Tag, "In discoveryOptionService()--");
//						Log.d(Tag,
//								"setDnsSdResponseListeners_onDnsSdServiceAvailable");
						Log.d(Tag, "Search to new service=>instanceName:" + instanceName
								+ "  registrationType:" + registrationType);
						// 探測到的服務，用裡面的參數去判斷是否是我們要連線的服務(Service)
						
						Log.d(Tag, "This is "+control_time+" times result~action");
						
						
						if(registrationType.equalsIgnoreCase(reg_Type+".local.")){
							
//							
							
							Log.d(Tag, "service=>instanceName:" + instanceName
									+ "  service_Instance:" + service_Instance);
							
							if(instanceName.equalsIgnoreCase(service_Instance) && now_slot == action_slot){
								
								
								WiFiP2pService service = new WiFiP2pService();
								service.device = srcDevice;
								service.instanceName = instanceName;
								service.serviceRegistrationType = registrationType;
								Log.d(Tag, "in slot:"+action_slot+"-connect to:"+service.device);
								IsServiceInRadius = true;
								
								int whenSuccessRole = -1;
//								if(reg_Type.equalsIgnoreCase("subtree root")){
//									//Set to bg_prev
//									whenSuccessRole = 2;
//									
//								}
								
//								if(P2p_Connect_Switch){
//									P2p_Connect_Switch = false;
//									Log.d(Tag, "Connect to "+service.toString());
//									connectP2pService(service, service_Instance, whenSuccessRole);
//								}
								
								
								
								
							}
							
							
						}
						
						
						
						

					}
				}, new DnsSdTxtRecordListener(){

					public void onDnsSdTxtRecordAvailable(String arg0,
							Map<String, String> arg1, WifiP2pDevice arg2){
						
						Log.d(Tag,
								"setDnsSdResponseListeners_onDnsSdTxtRecordAvailable");
					}
				});

		servicerequest = WifiP2pDnsSdServiceRequest.newInstance();
		
//		servicequest_typeall = WifiP2pServiceRequest.newInstance(WifiP2pServiceInfo.SERVICE_TYPE_ALL);
		
		manager.addServiceRequest(channel, servicerequest,
				new ActionListener(){

					@Override
					public void onFailure(int reason){
						
						Log.d(Tag, "addServiceRequest_Fail:" + reason);
					}

					@Override
					public void onSuccess(){
						
						Log.d(Tag, "addServiceRequest_Success");
					}

		});
		
//		
//		manager.addServiceRequest(channel, servicequest_typeall,
//				new ActionListener(){
//					
//					@Override
//					public void onFailure(int reason){
//						
//						Log.d(Tag, "addServiceRequest_typeall_Fail:" + reason);
//					}
//					
//					@Override
//					public void onSuccess(){
//						
//						Log.d(Tag, "addServiceRequest_typeall_Success");
//					}
//					
//		});
		
		
		
		
		
		manager.discoverServices(channel, new ActionListener(){

			@Override
			public void onFailure(int reason){
				Log.d(Tag, "discoverServices_Fail"+reason);
			}

			@Override
			public void onSuccess(){
				Log.d(Tag, "discoverServices_Success");
			}

		});
		

	}
	
	final HashMap<String, String> buddies = new HashMap<String, String>();
	
	public void discoveryServiceAndReaction(final String service_Instance, final int control_time){
		
		DnsSdTxtRecordListener txtrecordlistener = new DnsSdTxtRecordListener(){
			
			public void onDnsSdTxtRecordAvailable(String fullDomain,
					Map<String, String> record, WifiP2pDevice device){
				
				Log.d(Tag, "setDnsSdResponseListeners_onDnsSdTxtRecordAvailable");
				buddies.put(device.deviceAddress, record.get("buddyname"));
			}
		};
		
		DnsSdServiceResponseListener service_available = new DnsSdServiceResponseListener(){

			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice srcDevice){
				
				Log.d(Tag, "In discoveryOptionService()--");
				//The discovery service information
				Log.d(Tag, "Search to new service=>instanceName:" + instanceName
						+ "  registrationType:" + registrationType);
				
				String reg_Type_subtree_root = "subtree root"+".local.";
				String reg_Type_bg_prev = "bg_prev"+".local.";
//				String service_Instance = "RingNetwork_1";
				
				if(instanceName.equalsIgnoreCase(service_Instance)){
					
					if(registrationType.equalsIgnoreCase(reg_Type_subtree_root) && now_slot == 0){
						
						WiFiP2pService service = new WiFiP2pService();
						service.device = srcDevice;
						service.instanceName = instanceName;
						service.serviceRegistrationType = registrationType;
						Log.d(Tag, "in slot:"+now_slot+"-connect to:"+service.device+" it is subtree root");
						IsServiceInRadius = true;
						
						int whenSuccessRole = -1;
//						if(reg_Type.equalsIgnoreCase("subtree root")){
//							//Set to bg_prev
//							whenSuccessRole = 2;
//						}
						
//						if(P2p_Connect_Switch){
//							P2p_Connect_Switch = false;
//							Log.d(Tag, "Connect to "+service.toString());
//							connectP2pService(service, service_Instance, whenSuccessRole);
//						}
						
					}else if(registrationType.equalsIgnoreCase(reg_Type_bg_prev) && now_slot == 1){
						
						WiFiP2pService service = new WiFiP2pService();
						Log.d(Tag, "in slot:"+now_slot+"-connect to:"+service.device+" it is bg_prev");
						service.device = srcDevice;
						service.instanceName = instanceName;
						service.serviceRegistrationType = registrationType;
						IsServiceInRadius = true;
						
						int whenSuccessRole = -1;
//						if(reg_Type.equalsIgnoreCase("subtree root")){
//							//Set to bg_prev
//							whenSuccessRole = 2;
//						}
						
//						if(P2p_Connect_Switch){
//							P2p_Connect_Switch = false;
//							Log.d(Tag, "Connect to "+service.toString());
//							connectP2pService(service, service_Instance, whenSuccessRole);
//						}
						
					}
				}
			}
		};
		
		manager.setDnsSdResponseListeners(channel, service_available , txtrecordlistener);

		servicerequest = WifiP2pDnsSdServiceRequest.newInstance();
		
//		servicequest_typeall = WifiP2pServiceRequest.newInstance(WifiP2pServiceInfo.SERVICE_TYPE_ALL);
		
		manager.removeServiceRequest(channel, servicerequest, new ActionListener(){

			@Override
			public void onSuccess(){
				// TODO Auto-generated method stub
				Log.d(Tag, "removeServiceRequest_Success_1");
			}

			@Override
			public void onFailure(int reason){
				// TODO Auto-generated method stub
				Log.d(Tag, "removeServiceRequest_Fail_1:" + reason);
			}
			
		});
		
		manager.addServiceRequest(channel, servicerequest, new ActionListener(){

					@Override
					public void onFailure(int reason){
						Log.d(Tag, "addServiceRequest_Fail:" + reason);
						
					}

					@Override
					public void onSuccess(){
						Log.d(Tag, "addServiceRequest_Success");
					}

		});
		
		manager.discoverServices(channel, new ActionListener(){

			@Override
			public void onFailure(int reason){
				Log.d(Tag, "discoverServices_Fail"+reason);
			}

			@Override
			public void onSuccess(){
				Log.d(Tag, "discoverServices_Success");
			}

		});
		

	}
	
	
	
	
	
	public void OnlyDiscoveryService(){

		manager.discoverServices(channel, new ActionListener(){

			@Override
			public void onFailure(int reason){
				Log.d(Tag, "OnlydiscoverServices_Fail"+reason);
			}

			@Override
			public void onSuccess(){
				Log.d(Tag, "OnlydiscoverServices_Success");
			}

		});

	}

	public void connectP2p(WiFiP2pService service){
		Log.d(Tag, "Connect to:"+service.device.deviceAddress);
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = service.device.deviceAddress;
		
		config.wps.setup = WpsInfo.PBC;
		
		manager.connect(channel, config, new ActionListener(){
			
			@Override
			public void onSuccess(){
				Log.d(Tag, "Connecting to service");
				manager.stopPeerDiscovery(channel, new ActionListener(){

					@Override
					public void onFailure(int arg0){
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						
				}});
			}

			@Override
			public void onFailure(int errorCode){
				Log.d(Tag, "Failed connecting to service, ErrorCode:" + errorCode);
			}
		});
		
		if(servicerequest != null)
			manager.removeServiceRequest(channel, servicerequest,
					new ActionListener(){

						@Override
						public void onSuccess(){
							
						}

						@Override
						public void onFailure(int arg0){
							
						}
						
					});
		
	}
	
	public void connectP2pFromMAC(String MAC){
		
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = MAC;
		config.wps.setup = WpsInfo.PBC;
		
		manager.connect(channel, config, new ActionListener(){
			
			@Override
			public void onSuccess(){
				Log.d(Tag, "(MAC direct)Connecting to service");
				
				
//				manager.stopPeerDiscovery(channel, new ActionListener(){
//
//					@Override
//					public void onFailure(int arg0){
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onSuccess(){
//						// TODO Auto-generated method stub
//						
//				}});
				
				
			}

			@Override
			public void onFailure(int errorCode){
				Log.d(Tag, "(MAC direct)Failed connecting to service, ErrorCode:" + errorCode);
			}
		});
		
		
	}
	
	public void connectP2pService(WiFiP2pService service, String service_instance, final int whenSuccessRole){
		Log.d(Tag, "Connect to:"+service.device.deviceAddress);
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = service.device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		
		manager.connect(channel, config, new ActionListener(){
			
			@Override
			public void onSuccess(){
				Log.d(Tag, "Connecting to service");
//				network_process.change_role(whenSuccessRole);
				
				
//				manager.stopPeerDiscovery(channel, new ActionListener(){
//
//					@Override
//					public void onFailure(int arg0){
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void onSuccess(){
//						// TODO Auto-generated method stub
//						
//				}});
				
				
			}

			@Override
			public void onFailure(int errorCode){
				Log.d(Tag, "Failed connecting to service, ErrorCode:" + errorCode);
			}
		});
		
		
		if(servicerequest != null){
			manager.removeServiceRequest(channel, servicerequest,
					new ActionListener(){

						@Override
						public void onSuccess(){
							
						}

						@Override
						public void onFailure(int arg0){
							
						}
					});
		}
	}

	
    public void disconnect() {
        
        manager.removeGroup(channel, new ActionListener() {
        	
            @Override
            public void onFailure(int reasonCode) {
                Log.d(Tag, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
            	Log.d(Tag, "Disconnect success");
            	
            }

        });
        
        //Step
//        P2p_Connect_Switch = true;
        isGOwner = false;
        WifiP2pConnectState = false;
        IsServiceInRadius = false;
        FormNetTimer = 0;
    }

	public static boolean copyFile(InputStream inputStream, OutputStream out){
		byte buf[] = new byte[1024];
		int len;
		Log.d(Tag, "cf ch1");
		try{
			
			while((len = inputStream.read(buf)) != -1){
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();
		}catch(IOException e){
			Log.d(Tag, e.toString());
			Log.d(Tag, "cf ch2");
			return false;
		}
		Log.d(Tag, "cf ch3");
		return true;
	}
	
	public static boolean data_CopyFile(DataInputStream inputStream, DataOutputStream out){
		byte buf[] = new byte[1024];
		int len;
		Log.d(Tag, "cf ch1");
		try{
			
			while((len = inputStream.read(buf)) != -1){
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();
		}catch(IOException e){
			Log.d(Tag, e.toString());
			Log.d(Tag, "cf ch2");
			return false;
		}
		Log.d(Tag, "cf ch3");
		return true;
	}
	
	
/*
	public String GetClientIPv4(){
		String myIP = "";
		try{
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			while(en.hasMoreElements()){
				NetworkInterface ni = en.nextElement();
				Enumeration<InetAddress> en2 = ni.getInetAddresses();
				while(en2.hasMoreElements()){
					InetAddress inet = en2.nextElement();
					if(!inet.isLoopbackAddress()
							&& inet instanceof Inet4Address){
						myIP = inet.getHostAddress();
					}
				}
			}
		}catch(SocketException e){
			e.printStackTrace();
		}
		return myIP;
	}*/

	// PeerListListener
	@Override
	public void onPeersAvailable(WifiP2pDeviceList DeviceList){
		
		Log.d(Tag, "onPeersAvailable");
		
		Log.d(TestTag, "list all device:");
		
		int device_serial = 1;
		for(WifiP2pDevice device : DeviceList.getDeviceList()){
			
			Log.d(TestTag, device_serial+"' device:"+device.deviceName+" MAC:"+device.deviceAddress);
			device_serial++;
		}
		
		Result_Device_List = DeviceList;
		
		
		//*************************************************************************************************************************************
		/*
		if(P2p_Connect_Switch){
			WifiP2pConfig config = new WifiP2pConfig();
			boolean device_select = false;
			
			Log.d(TestTag, "Connect Process Start!");
			
			
			for(WifiP2pDevice device : DeviceList.getDeviceList()){
				if(device.deviceAddress.equals(Change_Action_Target_MAC_0) && Change_Action_Switch == 0){
					Log.d(TestTag, "Search to Target_MAC:"+Change_Action_Target_MAC_0);
					
					config.deviceAddress = Change_Action_Target_MAC_0;
					View_address = Change_Action_Target_MAC_0;
					device_select = true;
					
				}else if(device.deviceAddress.equals(Change_Action_Target_MAC_1) && Change_Action_Switch == 1){
					Log.d(TestTag, "Search to Target_MAC:"+Change_Action_Target_MAC_1);
					
					config.deviceAddress = Change_Action_Target_MAC_1;
					View_address = Change_Action_Target_MAC_1;
					
					device_select = true;
				}
			}
			
			
			if(device_select){
				Log.d(TestTag, "Selected Targer Device, then start connecting!");
				manager.connect(channel, config, new ActionListener(){
					
					@Override
					public void onSuccess(){
						// TODO Auto-generated method stub
						P2p_Connect_Switch = false;
						Log.d(TestTag, "Connect to "+View_address+" Success");
					}
					
					@Override
					public void onFailure(int reason){
						// TODO Auto-generated method stub
						Log.d(TestTag, "Connect failed, reason_code:"+reason);
					}
					
				});
				device_select = false;
			}
			
		}
		
		*/
	}

	// ChannelListener
	@Override
	public void onChannelDisconnected(){
		
	}

	// private ExecutorService exe = Executors.newSingleThreadExecutor();

	// ConnectionInfoListener
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo Info){
		
		Log.d(Tag, "onConnectionInfoAvailable");
		Log.d(Tag, "isGroupOwner:" + Info.isGroupOwner + "\n"
				+ Info.groupOwnerAddress);
		// Log.d(Tag, ""+Info.);

		new FileServerAsyncTask(this).executeOnExecutor(exec);
		
		// If is groupowner, wait handshark from client and get client ip
		// If is client, get ip itself and trans to groupowner
		
		WifiP2pConnectState = true;
		GInfo = Info;
		FormNetTimer = 0;

		// ServerSocket socket = new ServerSocket(3200);
		if(Info.isGroupOwner){
			isGOwner = true;
			
		}else{
			isGOwner = false;
			
			String myIP = device.deviceAddress;
			SendHandshake(MainClassCalendarActivity.this, myIP, Info);
			
			//*transport_photo*
//			if(transport_flag){
//				
//				08-26 14:39:42.441: D/ClassLogin(3688): NewPath:/storage/sdcard0/PhotoNoteDir/1_20150826_143942.jpg

//				08-26 14:44:09.186: D/ClassLogin(7210): File Uri:content://media/external/images/media/9881

				//send to group owner
//				String s_uri = "/storage/sdcard0/PhotoNoteDir/1_20150817_153310.jpg", s_fname = "1_20150817_153310.jpg";
//			String s_uri = "/storage/sdcard0/PhotoNoteDir/TempPhoto.jpg", s_fname = "TempPhoto.jpg";
			
			
//			Uri uri = Uri.parse("content://media/external/images/media/9881");
//			Log.d(TestTag, "TT_URI:" + uri);
//			ClassInformationPageActivity.sendFile((Context)MainClassCalendarActivity.this, 103, uri, 1, s_fname, Info.groupOwnerAddress.toString());
//			Log.d(TestTag, "server: transport to :" + Info.groupOwnerAddress);
			
//			ClassInformationNote_Fragment.mCallback.ShareNote_TypePhoto((Context)MainClassCalendarActivity.this, 103, s_fname, 1);
//				
//				
//			}
			
			
			

		}
		
	}

	// GroupInfoListener
	@Override
	public void onGroupInfoAvailable(WifiP2pGroup Group){
		
		Log.d(TestTag, "Group:"+Group);
		
		Collection<WifiP2pDevice> list = Group.getClientList();
		devicelist = new ArrayList<WifiP2pDevice>(list);

		Log.d(Tag, "onGroupInfoAvailable");
		for(int i = 0; i < devicelist.size(); i++){
			Log.d(Tag, "ClientList(" + i + "):"
					+ devicelist.get(i).deviceAddress);
		}
		if(isGOwner){
			Log.d(Tag, "InGO Open HandshakeHandle");
//			new HandshakeHandle(this).executeOnExecutor(exec);
		}
//		new FileServerAsyncTask(this).executeOnExecutor(exec);
//		exec.notifyAll();
		
	}
	
	
	
	public static void Remote_InputToSQL_Photo(Context context, int ClassSerial, String NoteFileName){
		//testtest(context, ClassSerial);
		int BelongClassID = getBelongClassID(context, ClassSerial);
		if(BelongClassID != 0){
			DataBaseManager DBManager = new DataBaseManager(context);
			SQLiteDatabase db = DBManager.getWritableDatabase();
			ContentValues content = new ContentValues();
			String TempPhotoTitle = NoteFileName;
			//NewPhotoPath();

			Calendar calendar = Calendar.getInstance();
			String today = calendar.get(Calendar.YEAR)
					+ LessThanTen(calendar.get(Calendar.MONTH) + 1)
					+ LessThanTen(calendar.get(Calendar.DAY_OF_MONTH));
			
			content.put(NOTE_TYPE, 1);
			content.put(NOTE_TYPEICON, R.drawable.photonoteicon_1);
			content.put(NOTE_FILENAME, TempPhotoTitle);
			content.put(CONTENT, NoteFileName);
			content.put(BELONG_CLASS_ID, BelongClassID);
			content.put(NOTE_TIME, today);

			db.insert(TABLE_NAME_NoteInformation, null, content);
			Log.i("Remote_InputToSQL_Photo_Tag", "Insert Successful!");
			Log.i("Remote_InputToSQL_Photo_Tag", "Insert Information: " + NOTE_FILENAME+"--"+ TempPhotoTitle + "  "+CONTENT+"--"+NoteFileName);

			DBManager.close();
		}
	}
	
	public static int getBelongClassID(Context context, int ClassSerial){
		int ClassID = 0;
		DataBaseManager DBManager = new DataBaseManager(context);
		SQLiteDatabase db = DBManager.getReadableDatabase();
		
		String SelectIDFromSerial = CLASS_SERIAL + " = '" + ClassSerial + "'";
		Cursor cursor = db.query(TABLE_NAME_TimeInformation,  new String[] {_ID}, SelectIDFromSerial, null, null, null, null);
		if(cursor.moveToNext()){
			ClassID = cursor.getInt(0);
		}
		Log.d("getBelongClassID()_1", "ClassIDFromSerial:" + ClassID);
		DBManager.close();
		return ClassID;
	}
	
//	Runnable runnable = () -> a = 10;
	
	
	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	public static class FileServerAsyncTask extends
			AsyncTask<Void, Void, String>{

		private static Context context;
		private int FileType = 1;
		private String Comefrom = "從";
		private int class_serial;
		
		String FileName = "";
		private String sourceAddress = "";

		/**
		 * @param context
		 * @param statusText
		 */
		@SuppressWarnings("static-access")
		public FileServerAsyncTask(Context context){
			this.context = context;
		}

		@Override
		protected String doInBackground(Void... params){
			try{
				ServerSocket serverSocket = new ServerSocket(FILETRANSFER_PORT);
				Log.d(Tag, "Server: FileServerAsyncTask Socket opened");
				
				serverSocket.setReuseAddress(true);
				
				
				//等待連線
				//while (true) {
				//接收連線
				Socket client = serverSocket.accept();
				DataInputStream in = null;
				try {
					Log.d(Tag, "Wait Data......");
					//接收資料
					in = new DataInputStream(client.getInputStream());
					
					class_serial = in.readInt();
					
					FileType = in.readInt();
					FileName = in.readUTF();
					Log.d(Tag, "R-CS:"+class_serial+"R-FileType:"+FileType+"  R-FileName:"+FileName);
					
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				//}
				String file_name_fragment ="";
				//依照檔案類型去分別動作
				if(FileType == 1){
					file_name_fragment = ".jpg";

				}else if(FileType == 2){
					file_name_fragment = ".amr";

				}
				final File f = new File(Environment.getExternalStorageDirectory() + "/"
						+ PhotoNoteDir + "/" + FileName);
						/*Environment.getExternalStorageDirectory() + "/"
								+ context.getPackageName() + "/wifip2pshared-"
								+ System.currentTimeMillis() + file_name_fragment);*/
				
				
				try {
					//接收檔案資料
					data_CopyFile(in, new DataOutputStream(new FileOutputStream(f)));
					Log.d(Tag, "R-File in copyfile:success?");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				File dirs = new File(f.getParent());
				if(!dirs.exists())
					dirs.mkdirs();
				f.createNewFile();
				
				
				
				sourceAddress = client.getInetAddress().toString();
				Log.d(Tag, "sourceAddress:"+sourceAddress);
				serverSocket.close();
				
				return f.getAbsolutePath();
			}catch(IOException e){
				Log.e(Tag, "Socket Error:"+e.getMessage());
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result){
			if(result != null){
				Log.d("Sending Time", "Receive Time:"+System.currentTimeMillis());
				//需更改
				Intent intent = new Intent();
				
				
				Uri uri = Uri.parse("file://" + result);
				Log.d(Tag, "server: NoteFileUri uri:" + uri);
				
				
				//依照檔案類型去分別動作
				if(FileType == 1){//photo
//					Remote_InputToSQL_Photo(context, class_serial, result);
					Remote_InputToSQL_Photo(context, class_serial, FileName);
					
//					intent.setAction(android.content.Intent.ACTION_VIEW);
//					intent.setDataAndType(uri, "image/*");
					
					intent.putExtra("ExPageType", 1);
					intent.putExtra("NoteFilePath", result);
					
					
					Log.d(TestTag, "received photo");

					Retransport_Switch = true;
					//Retransport_Times = 1;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					Retransport_Times = 2;
					Retransport_File_Name = FileName;
					
					
					
				}else if(FileType == 2){
//					intent.putExtra("ExType_OPEN", 1);
//					intent.putExtra("NoteFilePath", GetPathFromUri(uri));
					intent.putExtra("NoteFileUri", uri.toString());
					Log.d(Tag, "server: NoteFileUri uri:" + uri);
					intent.setClass(context, NoteVoicePlayActivity.class);
				}
				
				//收到轉發時啟動, In GroupOwner
				if(isGOwner){
					Log.d(TestTag, "Received Data!");
					for(WifiP2pDevice device : devicelist){
						if(!("/"+IPMap.get(device.deviceAddress)).equals(sourceAddress)){
							
							ClassInformationPageActivity.sendFile(context, class_serial, uri, FileType, FileName, IPMap.get(device.deviceAddress));
							Log.d(Tag, "server: transport to :" + IPMap.get(device.deviceAddress));
						}
					}
				}else{
					//send to group owner in next slot(next connect)
//					MainClassCalendarActivity.transport_flag = true;
					
					
					
				}
				
				new FileServerAsyncTask(context).executeOnExecutor(exec);
				
				
				if(FileType == 1){
					//View photo
//					context.startActivity(intent);
					
					intent.setClass(context, NoteShowPhotoActivity.class);
					
					context.startActivity(intent);
					
				}
				
				
			}

		}
		

		/*
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute(){

		}
	}

	public static class HandshakeHandle extends
			AsyncTask<Void, Void, InetAddress>{
		
		private Context context;
		private String MACIP;

		/**
		 * @param context
		 * @param statusText
		 */
		public HandshakeHandle(Context context){
			Log.d(Tag, "Server: ON1111");
			this.context = context;
		}

		@Override
		protected InetAddress doInBackground(Void... params){
			try{
				// Handshake Port:10153
				ServerSocket serverSocket = new ServerSocket(HANDSHAKE_PORT);
				Log.d(Tag, "Server: Handle Handshake Socket opened");
//				serverSocket.setReuseAddress(true);
				// Block code until connection opened
				
				Socket client = serverSocket.accept();
				InputStream in = client.getInputStream();
				BufferedInputStream ins = new BufferedInputStream(client.getInputStream());
				MACIP = ReadMessageFromBuffer(ins);
				
                Log.d(Tag, "來自" + client.getInetAddress() + "接收到的訊息 : " + MACIP);
				
				serverSocket.close();
				return client.getInetAddress();
			}catch(IOException e){
				Log.e(Tag, "Socket Error:"+e.getMessage());
				return null;
			}
		}

		

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(InetAddress result){
			if(result != null){
				Toast.makeText(context, "Handle ip:" + result.toString() + "MAC:" + MACIP, 5).show();
				SaveMACIP(context, MACIP, result);
			}
			new HandshakeHandle(context).executeOnExecutor(exec);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute(){
			// statusText.setText("Opening a server socket");
		}
	}
	
	
	
	public static String ReadMessageFromBuffer(BufferedInputStream in){
		byte buf[] = new byte[1024];
		String message = "";
		int len;
		try{
			while((len = in.read(buf)) != -1){
				message += new String(buf, 0, len);
				Log.d(Tag, "len:"+len);
			}
			Log.d(Tag, "ReadMessageFromBuffer message:"+message);
			
			in.close();
			in = null;							
		}catch(IOException e){
			Log.e(Tag, "ReadMessageFromBuffer Error");
			return null;
		}
		return message;
	}
	
	public static void SendMessage(String message, OutputStream oustream){
		
		try{
			Log.d(MainClassCalendarActivity.Tag, "SMTS_message:" + message);
			oustream.write(message.getBytes());
			oustream.flush();
			oustream.close();
		}catch(NumberFormatException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void SaveMACIP(Context context, String MacIP, InetAddress IP){
		
//		Log.d(TestTag, "MacIP:" + MacIP + " IP.toS:" + IP.getHostAddress());
//		IPMap.put(MacIP, IP.getHostAddress());
		
//		if(transport_flag){
//			//send to all client
//			String s_uri = "/storage/sdcard0/PhotoNoteDir/1_20150817_153310.jpg", s_fname = "1_20150817_153310.jpg";
//			Uri uri = Uri.parse("file://" + s_uri);
//			
//			ClassInformationPageActivity.sendFile(context, 103, uri, 1, s_fname, IP.getHostAddress());
//			Log.d(Tag, "server: transport to :" + IP.getHostAddress());
//			
//		}
		
	}
	
	public void SetSlot(int slot){
		now_slot = slot;
	}
	
	public static boolean getisGOwner(){
		return isGOwner;
	}
	
	public static boolean getP2pState(){
		return WifiP2pConnectState;
	}
	
	public static WifiP2pInfo getWifiP2pInfo(){
		return GInfo;
	}
	
	public static Map<String, String> getIPMap(){
		return IPMap;
	}
	
	public static ArrayList<WifiP2pDevice> getDeviceList(){
		return devicelist;
	}
	
	public String ErrorCode(int reason){
		switch(reason){
		case 0:
			return "P2P_UNSUPPORTED";
		case 1:
			return "ERROR";
		case 2:
			return "BUSY";
		default:
			return "Error reason";
		}
	}
}
