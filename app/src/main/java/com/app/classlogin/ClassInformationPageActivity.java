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

import static com.app.classlogin.CommonMethod.LessThanTen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.app.classlogin.ClassInformationNote_Fragment.HandingShareActionListener;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ClassInformationPageActivity extends ActionBarActivity implements
		HandingShareActionListener{

	public static final int InformationPage = 0, NotePage = 1,
			PeerListPage = 2;
	public static final int NONE = 0, GET_PHOTO = 1, SAVE_PHOTO = 2;

	public static final String TXTRECORD_PROP_AVAILABLE = "available";
	public static final String SERVICE_INSTANCE = "ClassLogin";
	public static final String SERVICE_REG_TYPE = "_Name._StartDay";

	private DataBaseManager DBManager;
	private int BelongClassID, FromCalendar = 0, DefaultFragment;
	private int[] EventTime = new int[3];

	public static String PhotoNoteDir = "PhotoNoteDir",
			TempPhotoFileName = "TempPhoto.jpg", TempPhotoTitle, DetectFileStr;

	private Calendar calendar;
	private Fragment ClassInformationNote_Fragment;
	private static int class_Serial;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classinformation_page);

		final ActionBar actionbar = getActionBar();
		actionbar.setTitle("新增筆記");
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Intent intent = getIntent();
		BelongClassID = intent.getIntExtra("BelongClassID", 0);
		EventTime = intent.getIntArrayExtra("EventTime");
		FromCalendar = intent.getIntExtra("FromCalendar", 0);
		DefaultFragment = intent
				.getIntExtra("DefaultFragment", InformationPage);

		Log.i("Check_2", "BelongClassID:" + BelongClassID);

		// class_Serial

		class_Serial = getClassSerial(BelongClassID);
		Log.i("Check_2", "class_Serial:" + class_Serial);

		// Add Tab
		Fragment ClassInformationInformation_Fragment = new ClassInformationInformation_Fragment();
		actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.ClassInformationStr)
				.setTabListener(
						new MainClassCalendarTabListener(
								ClassInformationInformation_Fragment)));
		((com.app.classlogin.ClassInformationInformation_Fragment) ClassInformationInformation_Fragment)
				.SetClassIDMethod(BelongClassID);

		ClassInformationNote_Fragment = new ClassInformationNote_Fragment();
		actionbar.addTab(actionbar
				.newTab()
				.setText(R.string.ClassInformationNoteStr)
				.setTabListener(
						new MainClassCalendarTabListener(
								ClassInformationNote_Fragment)));

		// actionbar.setSelectedNavigationItem(1);//預設選擇
		// Notice this code !!
		((com.app.classlogin.ClassInformationNote_Fragment) ClassInformationNote_Fragment)
				.SetClassIDMethod(BelongClassID);

		/*
		 * //照相筆記TAB Fragment ClassInformationPhotoNote_Fragment = new
		 * ClassInformationPhotoNote_Fragment(); actionbar.addTab(actionbar
		 * .newTab() .setText(R.string.ClassInformationPhotoNoteStr)
		 * .setTabListener( new MainClassCalendarTabListener(
		 * ClassInformationPhotoNote_Fragment)));
		 */

		// Set Default Fragment
		actionbar.setSelectedNavigationItem(DefaultFragment);

		if(FromCalendar == 1){
			actionbar.setSelectedNavigationItem(1);
			((com.app.classlogin.ClassInformationNote_Fragment) ClassInformationNote_Fragment)
					.SetFromCalendar(FromCalendar);
			((com.app.classlogin.ClassInformationNote_Fragment) ClassInformationNote_Fragment)
					.SetTodayDate(EventTime);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.option, menu);
		MenuItem menuitem = menu.findItem(R.id.action_insert);
		// menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		Intent intent = new Intent();

		switch (item.getItemId()){

		case R.id.action_photo:
			PhotoFunction(intent);
			break;

		case R.id.action_rec:
			intent.putExtra("BelongClassID", BelongClassID);
			intent.putExtra("EventTime", EventTime);

			intent.setClass(ClassInformationPageActivity.this,
					NoteRecVoiceManagerActivity.class);
			NoteRecVoiceManagerActivity.SC(getContentResolver());
			startActivity(intent);
			finish();
			break;

		case R.id.action_insert:
			intent.putExtra("BelongClassID", BelongClassID);
			intent.putExtra("PageType", 0);// 0 = insert, 1 = read
			intent.setClass(ClassInformationPageActivity.this,
					NoteInsertPageActivity.class);
			startActivity(intent);
			finish();
			break;

		/*
		 * case R.id.action_settings: break;
		 */
		}
		return super.onOptionsItemSelected(item);
	}

	public int getClassSerial(int BelongClassID){
		int serial = 0;
		DataBaseManager DBManager = new DataBaseManager(
				ClassInformationPageActivity.this);
		SQLiteDatabase db = DBManager.getReadableDatabase();

		String SelectIDFromSerial = _ID + " = '" + BelongClassID + "'";
		Cursor cursor = db.query(TABLE_NAME_TimeInformation,
				new String[] { CLASS_SERIAL }, SelectIDFromSerial, null, null,
				null, null);
		Log.d("getClassSerial()()", "Count:" + cursor.getCount());
		if(cursor.moveToNext()){
			serial = cursor.getInt(0);
		}
		Log.d("getClassSerial()", "Serial:" + serial);
		DBManager.close();
		return serial;
	}

	private void PhotoFunction(Intent intent){
		String states = Environment.getExternalStorageState();
		if(states.equals(Environment.MEDIA_MOUNTED)){
			try{
				File dir = new File(Environment.getExternalStorageDirectory()
						+ "/" + PhotoNoteDir);
				if(!dir.exists()){
					dir.mkdir();
				}
				intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				DetectFileStr = Environment.getExternalStorageDirectory() + "/"
						+ PhotoNoteDir + "/" + TempPhotoFileName;
				File Temp = new File(DetectFileStr);
				Uri u = Uri.fromFile(Temp);

				// 照片方向
				intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
				startActivityForResult(intent, GET_PHOTO);

			}catch(ActivityNotFoundException e){
				Toast.makeText(ClassInformationPageActivity.this, "沒有找到儲存目錄",
						Toast.LENGTH_LONG).show();
			}

		}else{
			Toast.makeText(ClassInformationPageActivity.this, "沒有儲存卡",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

		if(resultCode == RESULT_OK){
			switch (requestCode){
			case GET_PHOTO:
				File Temp = new File(Environment.getExternalStorageDirectory()
						+ "/" + PhotoNoteDir + "/" + TempPhotoFileName);

				try{

					Uri Note_Uri = Uri
							.parse(android.provider.MediaStore.Images.Media
									.insertImage(getContentResolver(),
											Temp.getAbsolutePath(), null, null));

					String NotePhotoPath = GetPathFromUri(Note_Uri);
					Log.d(MainClassCalendarActivity.Tag, "Path:"+NotePhotoPath);

					String NewFileName = NewPhotoPath();
					CopyPhotoToMyDir(NotePhotoPath,
							Environment.getExternalStorageDirectory() + "/"
									+ PhotoNoteDir + "/" + NewFileName);
					Log.d(MainClassCalendarActivity.Tag, "NewPath:"+Environment.getExternalStorageDirectory()
							+ "/" + PhotoNoteDir + "/" + NewFileName);
					Log.d(MainClassCalendarActivity.Tag, "VFURI_1:" + Note_Uri);

					InputToSQL_Photo(NewFileName);
					// Set can or can't transport file

					checkAndSend(this, class_Serial, Note_Uri, NewFileName, 1);

				}catch(FileNotFoundException e){
					e.printStackTrace();
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

		// Refresh**
		// String Select = BELONG_CLASS_ID + " ='" + BelongClassID + "'";
		// ((com.app.classlogin.ClassInformationNote_Fragment)
		// ClassInformationNote_Fragment).SetListView(Select);
	}

	public static void sendFile(Context context, int class_serial, Uri FileUri,
			int FileType, String FileName, String dstAddress){
		Intent serviceIntent = new Intent(context, FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);

		// serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_COMEFROM, );
		serviceIntent.putExtra(FileTransferService.EXTRAS_CLASS_SERIAL,
				class_serial);

		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
				FileUri.toString());
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_TYPE, FileType);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_NAME, FileName);

		Log.d(MainClassCalendarActivity.Tag, "InFT:" + FileType + "  InFN:"
				+ FileName);
		// if(MainClassCalendarActivity.getisGOwner() && dstAddress != null){
		// serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
		// MainClassCalendarActivity.getIPMap().get(dstAddress));
		// }else{}

		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				dstAddress);
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
				MainClassCalendarActivity.FILETRANSFER_PORT);
		context.startService(serviceIntent);
	}

	// FileType -> 0 = Text, 1 = Photo, 2 = Voice
	public static void checkAndSend(Context context, int class_serial, Uri uri,
			String FileName, int FileType){
		if(MainClassCalendarActivity.getP2pState() == true){
			// WifiP2p's connect state
			Log.d(MainClassCalendarActivity.Tag, "MCCA.getP2pState:"
					+ MainClassCalendarActivity.getP2pState());
			if(MainClassCalendarActivity.getisGOwner()){
				Log.d(MainClassCalendarActivity.Tag, "MCCA.getisGOwner:"
						+ MainClassCalendarActivity.getisGOwner());
				// is GroupOwner
				Map<String, String> IpMap = MainClassCalendarActivity
						.getIPMap();
				ArrayList<WifiP2pDevice> DeviceList = MainClassCalendarActivity
						.getDeviceList();

				Log.d(MainClassCalendarActivity.Tag, "Sending File URI:" + uri);
				for(WifiP2pDevice device : DeviceList){
					sendFile(context, class_serial, uri, FileType, FileName,
							IpMap.get(device.deviceAddress));
				}
			}else{
				// Client
				WifiP2pInfo Info = MainClassCalendarActivity.getWifiP2pInfo();
				sendFile(context, class_serial, uri, FileType, FileName,
						Info.groupOwnerAddress.getHostAddress());
			}

		}
	}

	@Override
	public void ShareNote_TypePhoto(Context context, int BelongClassID, String FileName,
			int FileType){
		int class_serial = 0;
		Uri uri = null;
		File Temp = new File(Environment.getExternalStorageDirectory() + "/"
				+ PhotoNoteDir + "/" + FileName);
		class_serial = getClassSerial(BelongClassID);
		
		try{
			uri = Uri.parse(android.provider.MediaStore.Images.Media
					.insertImage(getContentResolver(), Temp.getAbsolutePath(),
							null, null));
		}catch(FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(uri != null){
			checkAndSend(context, class_serial, uri, FileName, FileType);
		}else{
			Log.e("ShareNote_TypePhoto", "uri error, can't be null!");
		}

	}
	
	public static int getClassSerial(Context context, int BelongClassID, String FileName){
		int class_serial = 0;
		DataBaseManager DBManager = new DataBaseManager(context);
		SQLiteDatabase db = DBManager.getReadableDatabase();
		
		String SelectIDFromSerial = _ID + " = '" + BelongClassID + "'";
		Cursor cursor = db.query(TABLE_NAME_TimeInformation,  new String[] {CLASS_SERIAL}, SelectIDFromSerial, null, null, null, null);
		if(cursor.moveToNext()){
			class_serial = cursor.getInt(0);
		}
		Log.d("getBelongClassID()_1", "ClassIDFromSerial:" + class_serial);
		
		DBManager.close();
		return class_serial;
	}

	private void InputToSQL_Photo(String NoteFileName){
		DBManager = new DataBaseManager(this);
		SQLiteDatabase db = DBManager.getWritableDatabase();
		ContentValues content = new ContentValues();

		calendar = Calendar.getInstance();
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
		Log.i("PhotoSQL_Tag", "Insert Successful!");

		DBManager.close();
	}

	private String NewPhotoPath(){
		Calendar calendar = Calendar.getInstance();
		String FileName;
		FileName = BelongClassID + "_" + calendar.get(Calendar.YEAR)
				+ LessThanTen(calendar.get(Calendar.MONTH) + 1)
				+ LessThanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "_"
				+ LessThanTen(calendar.get(Calendar.HOUR_OF_DAY))
				+ LessThanTen(calendar.get(Calendar.MINUTE))
				+ LessThanTen(calendar.get(Calendar.SECOND));
		TempPhotoTitle = FileName;
		FileName = FileName + ".jpg";
		return FileName;
	}

	private String GetPathFromUri(Uri note_Uri){
		String[] FilePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(note_Uri, FilePathColumn,
				null, null, null);
		cursor.moveToFirst();
		int ColumnIndex = cursor.getColumnIndex(FilePathColumn[0]);
		String NotePhotoPath = cursor.getString(ColumnIndex);
		cursor.close();
		return NotePhotoPath;
	}

	private void CopyPhotoToMyDir(String Source, String Destination){
		int bytesum = 0;
		int byteread = 0;
		File SourceFile = new File(Source);
		try{
			if(SourceFile.exists()){
				InputStream input = new FileInputStream(Source);
				FileOutputStream output = new FileOutputStream(Destination,
						false);

				byte[] buffer = new byte[1024];

				while((byteread = input.read(buffer)) != -1){
					bytesum += byteread;
					output.write(buffer, 0, byteread);
				}
				output.flush();
				output.close();
				input.close();

			}
		}catch(Exception e){
			Toast.makeText(ClassInformationPageActivity.this, "複製檔案失敗",
					Toast.LENGTH_LONG).show();
		}
	}

}
