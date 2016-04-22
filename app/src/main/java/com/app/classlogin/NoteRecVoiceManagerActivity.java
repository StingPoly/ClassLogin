package com.app.classlogin;

import static com.app.classlogin.ClassDataStructure_ClassNote.BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.CONTENT;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TIME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPE;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPEICON;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;
import static com.app.classlogin.ClassInformationPageActivity.NotePage;
import static com.app.classlogin.CommonMethod.LessThanTen;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

;

public class NoteRecVoiceManagerActivity extends Activity{

	protected int BelongClassID;
	private DataBaseManager DBManager;
	private ImageButton RecControlButton, RecPauseButton;
	private TextView TimeView;
	private int ControlFlag = 0;// 0 = Nothing, 1 = Starting
	private Long StartTime;
	private String VoiceNoteDir = "VoiceNoteDir", NoteTitle, FileName;
	private Uri uri;
	private MediaRecorder mediaRecorder = null;
	private Handler handler = new Handler();
	private Calendar calendar;
	private int[] EventTime = new int[3];
	
	private static ContentResolver mContentResolver;
	
	public static void SC(ContentResolver mCR){
		mContentResolver = mCR;
	}

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_recvoice_manager_page);

		RecControlButton = (ImageButton) findViewById(R.id.RecControlButton);
		RecPauseButton = (ImageButton) findViewById(R.id.RecPauseButton);
		TimeView = (TextView) findViewById(R.id.RecTimeView);

		Intent intent = getIntent();
		BelongClassID = intent.getIntExtra("BelongClassID", 0);
		EventTime = intent.getIntArrayExtra("EventTime");

		File VoiceDirPath = new File(Environment.getExternalStorageDirectory()
				+ "/" + VoiceNoteDir);
		if(!VoiceDirPath.exists()){
			VoiceDirPath.mkdir();
		}

		RecControlButton.setOnClickListener(new OnClickListener(){
			
			File VoiceFile;
			String TempFileName;
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v){
				// TODO Auto-generated method stub
				if(ControlFlag == 0){
					TempFileName = NewVoiceFileName();
					FileName = Environment.getExternalStorageDirectory() + "/"
							+ VoiceNoteDir + "/" + TempFileName;
					VoiceFile = new File(FileName);
					
					
					Log.i("VoiceFileTag", "VoiceFile.getAbsolutePath():"+VoiceFile.getAbsolutePath());

					Log.i("VoiceFileTag", "FileName:"+FileName);

					// Setting MediaRecorder
					mediaRecorder = new MediaRecorder();
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mediaRecorder
							.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
					mediaRecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					mediaRecorder.setOutputFile(VoiceFile.getAbsolutePath());
					try{
						mediaRecorder.prepare();
						mediaRecorder.start();
					}catch(IOException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Open Time Count
					TimeView.setText("00:00:00");
					StartTime = System.currentTimeMillis();
					handler.removeCallbacks(updateTimer);
					handler.postDelayed(updateTimer, 1000);

					ControlFlag = 1;
					RecControlButton.setImageResource(R.drawable.stopbutton);
				}else{

					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder = null;

					// Close Time Count
					if(handler != null){
						handler.removeCallbacks(updateTimer);
					}
					InputToSQL(FileName);
//					uri = Uri.parse(android.provider.MediaStore.Audio.Media.getContentUriForPath(FileName).toString());
//					uri = android.provider.MediaStore.Audio.Media.getContentUriForPath(FileName);
					
//					uri = Uri.fromFile(new File(FileName));
//					uri = Uri.parse(new File(FileName).toString());
//					uri = Uri.parse(VoiceFile.toString());//無用
//					uri = VoiceFile.toURI();
					uri = Uri.fromFile(VoiceFile);
//					Uri uri2 = Uri.parse(android.provider.MediaStore.Audio)
							
					Log.i("RecVoice", "File Path_1" + VoiceFile);
					Log.i("RecVoice", "File Path_2" + FileName);
					
					Log.d("RecVoice", VoiceFile.getAbsolutePath());
					
//					uri = 
//					uri = Uri.fromFile(VoiceFile);
					Log.d("RecVoice", "VFURI_1:"+uri + 
							"\nVFURI_3:"+Uri.fromFile(new File(FileName))+
							
							"\nVFURI_5:"+Uri.fromFile(VoiceFile)+
							"\nVFURI_6:"+android.provider.MediaStore.Audio.Media.getContentUriForPath(VoiceFile.getAbsolutePath())+
							"\nVFURI_T7:"+android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
							/*"\nVFURI_7:"+Uri.parse(android.provider.MediaStore.i)*/);
					
					
					Log.d("RecVoice", "");
					
					
//					ClassInformationPageActivity.checkAndSend(NoteRecVoiceManagerActivity.this, uri, TempFileName, MainClassCalendarActivity.TYPE_VOICE);
					
					
					ControlFlag = 0;
					RecControlButton.setImageResource(R.drawable.recbutton);
				}
			}

		});
		

		RecPauseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				// TODO Auto-generated method stub

			}

		});

	}

	private Runnable updateTimer = new Runnable(){

		@Override
		public void run(){
			// TODO Auto-generated method stub
			final TextView time = (TextView) findViewById(R.id.RecTimeView);
			Long spendTime = System.currentTimeMillis() - StartTime;
			// 計算目前已過分鐘數
			Long min = (spendTime / 1000) / 60;
			Long hour = min / 60;
			// 計算目前已過秒數
			Long seconds = (spendTime / 1000) % 60;
			time.setText(LessThanTen(hour) + ":" + LessThanTen(min) + ":"
					+ LessThanTen(seconds));
			handler.postDelayed(this, 1000);
		}

	};

	private void InputToSQL(String NoteFileName){
		// TODO Auto-generated method stub
		DBManager = new DataBaseManager(this);
		SQLiteDatabase db = DBManager.getWritableDatabase();
		ContentValues content = new ContentValues();

		calendar = Calendar.getInstance();
		String today = calendar.get(Calendar.YEAR)
				+ LessThanTen(calendar.get(Calendar.MONTH) + 1)
				+ LessThanTen(calendar.get(Calendar.DAY_OF_MONTH));

		content.put(NOTE_TYPE, 2);
		content.put(NOTE_TYPEICON, R.drawable.voicenoteicon_1);
		content.put(NOTE_FILENAME, NoteTitle);
		content.put(CONTENT, FileName);
		content.put(BELONG_CLASS_ID, BelongClassID);
		content.put(NOTE_TIME, today);

		db.insert(TABLE_NAME_NoteInformation, null, content);
		Log.i("PhotoSQL_Tag", "Insert Successful!");

		DBManager.close();
	}

	private String NewVoiceFileName(){
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		NoteTitle = BelongClassID + "_" + calendar.get(Calendar.YEAR)
				+ LessThanTen(calendar.get(Calendar.MONTH) + 1)
				+ LessThanTen(calendar.get(Calendar.DAY_OF_MONTH)) + "_"
				+ LessThanTen(calendar.get(Calendar.HOUR_OF_DAY))
				+ LessThanTen(calendar.get(Calendar.MINUTE))
				+ LessThanTen(calendar.get(Calendar.SECOND));
		FileName = NoteTitle + ".amr";
		return FileName;
	}

	public boolean onKeyDown(int KeyCode, KeyEvent event){

		if(KeyCode == KeyEvent.KEYCODE_BACK){

			Intent intent = new Intent();
			intent.putExtra("BelongClassID", BelongClassID);
			intent.putExtra("EventTime", EventTime);
			intent.putExtra("DefaultFragment", NotePage);
			intent.setClass(NoteRecVoiceManagerActivity.this,
					ClassInformationPageActivity.class);
			startActivity(intent);
			finish();

			return true;
		}

		return super.onKeyDown(KeyCode, event);
	}
	
	
	private String GetPathFromUri(Uri note_Uri, ContentResolver mContentResolver){
		String[] FilePathColumn = { MediaStore.Audio.Media.DATA };
		
		Cursor cursor = mContentResolver.query(note_Uri, FilePathColumn,
				null, null, null);
		cursor.moveToFirst();
		int ColumnIndex = cursor.getColumnIndex(FilePathColumn[0]);
		String NotePhotoPath = cursor.getString(ColumnIndex);
		cursor.close();
		return NotePhotoPath;
	}
}
