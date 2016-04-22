package com.app.classlogin;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.CONTENT;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TIME;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

public class NoteShowPhotoActivity extends Activity{
	
	private String PhotoNoteDir = "PhotoNoteDir", Title, PhotoFileName, PhotoDataPath, FileDir;
	private DataBaseManager DBManager = null;
	private ImageView PhotoNote;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_show_page);
		PhotoNote = (ImageView)findViewById(R.id.PhotoNoteView);
		Intent intent = getIntent();
		FileDir = Environment.getExternalStorageDirectory() + "/" + PhotoNoteDir + "/";
		ActionBar actionbar = getActionBar();
		actionbar.hide();
		
		Title = intent.getStringExtra("NoteTitle");
		Timer t = new Timer();
		
		String extra_photo = intent.getStringExtra("NoteFilePath");
		int ExPT = intent.getIntExtra("ExPageType", 0);
		if(ExPT == 1){
			PutImageInView(extra_photo);
			t.schedule(new Back_Action(), 800);
		}else{
			ReadNote(Title);
			
		}
	}
	
	private void ReadNote(String TITLE){
	    DBManager = new DataBaseManager(this);
		SQLiteDatabase db = DBManager.getReadableDatabase();
		String Select = NOTE_FILENAME + " = '" + TITLE + "'";
		Cursor cursor = db.query(TABLE_NAME_NoteInformation, new String[] { _ID, NOTE_FILENAME, CONTENT, NOTE_TIME, BELONG_CLASS_ID}
								, Select, null, null, null, null);
		cursor.moveToFirst();
		PhotoFileName = cursor.getString(2);
		PhotoDataPath = FileDir + PhotoFileName;
		PutImageInView(PhotoDataPath);
		//Log.i("ImagePath", PhotoDataPath);
		DBManager.close();
	}

	private void PutImageInView(String PhotoPath) {
		Log.i("ImagePath", PhotoPath);
		Bitmap bitmap = BitmapFactory.decodeFile(PhotoPath);
		int hight = bitmap.getHeight();
		int width = bitmap.getWidth();
		Log.i("ImageSize", "H:" + hight + "W:" + width);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap ViewImage = Bitmap.createBitmap(bitmap, 0, 0, width, hight, matrix, true);
		PhotoNote.setImageBitmap(ViewImage);
		
		
	}
	
	
	public class Back_Action extends TimerTask{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			Instrumentation inst = new Instrumentation();
			inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		}
		
	}
}
