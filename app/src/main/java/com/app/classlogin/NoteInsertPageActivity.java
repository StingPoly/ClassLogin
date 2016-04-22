package com.app.classlogin;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.CONTENT;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TIME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPE;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPEICON;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;
import static com.app.classlogin.ClassInformationPageActivity.NotePage;
import static com.app.classlogin.CommonMethod.LessThanTen;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class NoteInsertPageActivity extends ActionBarActivity{

	EditText ContentNote, TitleText;
	private DataBaseManager DBManager = null;
	String BelongClass, Title;
	int Type, BelongClassID;
	MenuItem saveitem, edititem;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_note_insert_page);
		ContentNote = (EditText) findViewById(R.id.NoteContenteditText);
		TitleText = (EditText) findViewById(R.id.NoteTitleeditText);
		Intent intent = getIntent();
		Type = intent.getIntExtra("PageType", 0);
		BelongClassID = intent.getIntExtra("BelongClassID", 0);

		if(Type == 1){// update
			Title = intent.getStringExtra("NoteTitle");
			ReadNote(Title);
			TitleText.setEnabled(true);
			ContentNote.setEnabled(true);

		}else if(Type == 2){// read
			Title = intent.getStringExtra("NoteTitle");
			ReadNote(Title);
			Log.i("Reading Insert", "Reading");
			TitleText.setEnabled(false);
			TitleText.setTextColor(Color.BLACK);
			ContentNote.setEnabled(false);
			ContentNote.setTextColor(Color.BLACK);
		}

	}

	private void SaveOrUpdateInDB(){
		DBManager = new DataBaseManager(this);
		SQLiteDatabase db = DBManager.getWritableDatabase();
		ContentValues content = new ContentValues();

		Calendar calendar = Calendar.getInstance();
		String today = calendar.get(Calendar.YEAR)
				+ LessThanTen(calendar.get(Calendar.MONTH) + 1)
				+ LessThanTen(calendar.get(Calendar.DAY_OF_MONTH));

		content.put(NOTE_TYPE, 0);
		content.put(NOTE_TYPEICON, R.drawable.textnoteicon_2);
		content.put(NOTE_FILENAME, TitleText.getText().toString());
		content.put(CONTENT, ContentNote.getText().toString());
		content.put(BELONG_CLASS_ID, BelongClassID);// *****
		content.put(NOTE_TIME, today);

		if(Type == 0){// 0 = insert, 1 = update, 2 = read
			db.insert(TABLE_NAME_NoteInformation, null, content);
		}else if(Type == 1){
			String Select = NOTE_FILENAME + " = '" + Title + "'";
			db.update(TABLE_NAME_NoteInformation, content, Select, null);
		}

	}

	private void ReadNote(String TITLE){
		DBManager = new DataBaseManager(this);
		SQLiteDatabase db = DBManager.getReadableDatabase();
		String Select = NOTE_FILENAME + " = '" + TITLE + "'";
		Cursor cursor = db.query(TABLE_NAME_NoteInformation, new String[] {
				_ID, NOTE_FILENAME, CONTENT, NOTE_TIME, BELONG_CLASS_ID,
				NOTE_TYPE }, Select, null, null, null, null);
		// PutInSpinner(cursor, BELONG_CLASS_ID);
		cursor.moveToFirst();
		TitleText.setText(cursor.getString(1));
		ContentNote.setText(cursor.getString(2));
		DBManager.close();
	}

	public boolean onKeyDown(int KeyCode, KeyEvent event){

		if(Type != 2){
			if(KeyCode == KeyEvent.KEYCODE_BACK){
				new AlertDialog.Builder(this)
						.setTitle("確定離開嗎?")
						.setPositiveButton("離開",
								new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
											int which){
										// TODO Auto-generated method stub
										Intent intent = new Intent();
										intent.putExtra("BelongClassID",
												BelongClassID);
										intent.putExtra("DefaultFragment",
												NotePage);
										intent.setClass(
												NoteInsertPageActivity.this,
												ClassInformationPageActivity.class);
										startActivity(intent);
										finish();
									}
								}).setNegativeButton("取消", null).show();
				return true;
			}
		}else{
			Intent intent = new Intent();
			intent.putExtra("BelongClassID", BelongClassID);
			intent.putExtra("DefaultFragment", NotePage);
			intent.setClass(NoteInsertPageActivity.this,
					ClassInformationPageActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(KeyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.note_insert_page_menu, menu);
		saveitem = menu.findItem(R.id.action_savenote);
		if(Type == 2){
			saveitem.setEnabled(false);
		}else{
			saveitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_savenote:
			SaveOrUpdateInDB();
			Intent intent = new Intent();
			intent.putExtra("BelongClassID", BelongClassID);// 0
			intent.setClass(NoteInsertPageActivity.this,
					ClassInformationPageActivity.class);
			startActivity(intent);
			finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onDestory(){
		finish();
		super.onDestroy();
	}

}
