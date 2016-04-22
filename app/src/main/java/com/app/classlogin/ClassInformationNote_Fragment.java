package com.app.classlogin;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.app.classlogin.MainClassCalendarFragment.HandingDateSeletedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.TABLE_NAME_CalendarTimeInformation;
import static com.app.classlogin.ClassDataStructure_ClassNote.*;
import static com.app.classlogin.CommonMethod.*;
import static android.provider.BaseColumns._ID;

public class ClassInformationNote_Fragment extends Fragment{

	private DataBaseManager DBManager = null;
	public String ClassName, NoteName, BelongClass_SELECT;
	public String[] NoteNameArray = new String[3000],
			FilePathArray = new String[3000];
	int ClassID, FromCalendar = 0;
	int[] NoteTypeArray = new int[3000], NoteBelongIdArray = new int[3000],
			EventTime = new int[3];
	Cursor cursor;
	ListView NoteList;
	
	public static HandingShareActionListener mCallback;
	
	public interface HandingShareActionListener{
		public void ShareNote_TypePhoto(Context context, int BelongClassID, String FileName, int FileType);
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
            mCallback = (HandingShareActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}

	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		
		this.DBManager = new DataBaseManager(this.getActivity());
		Log.i("TestTransTag", "ClassID" + ClassID);
		BelongClass_SELECT = BELONG_CLASS_ID + " = '" + ClassID + "'";

		if(FromCalendar == 0){
			SetListView(BelongClass_SELECT);
		}else if(FromCalendar == 1){
			SetDateListView(BelongClass_SELECT);
		}

		// NoteList = (ListView)getView().findViewById(R.id.list_item);
		NoteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id){
				// TODO Auto-generated method stub
				Log.i("ClickTag", "NoteNameArray[" + position + "]:"
						+ NoteNameArray[position] + " ID:" + id);
				SelectList(position);
				return true;
			}
		});

		NoteList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id){
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("PageType", 2);
				intent.putExtra("BelongClassID", NoteBelongIdArray[position]);
				intent.putExtra("NoteTitle", NoteNameArray[position]);
				intent.putExtra("NoteFilePath", FilePathArray[position]);
				Log.d("ITA", FilePathArray[position]);

				if(NoteTypeArray[position] == 0){ // Text Note
					intent.setClass(getActivity(), NoteInsertPageActivity.class);
				}else if(NoteTypeArray[position] == 1){ // Photo Note
					intent.setClass(getActivity(), NoteShowPhotoActivity.class);
				}else if(NoteTypeArray[position] == 2){ // Voice Note
					intent.setClass(getActivity(), NoteVoicePlayActivity.class);
				}
				startActivity(intent);
				if(NoteTypeArray[position] == 0){
					getActivity().finish();
				}

			}
		});

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View fragmentView = inflater.inflate(R.layout.notelistview_in_fragment,
				container, false);
		NoteList = (ListView) fragmentView
				.findViewById(R.id.NoteListViewInFragment);
		return fragmentView;
	}

	public void SetListView(String Select){
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();

		cursor = db.query(TABLE_NAME_NoteInformation, new String[] { _ID,
				NOTE_FILENAME, CONTENT, NOTE_TIME, BELONG_CLASS_ID, NOTE_TYPE,
				NOTE_TYPEICON }, Select, null, null, null, null);

		int index = 0;
		while(cursor.moveToNext()){
			NoteName = cursor.getString(1);
			NoteNameArray[index] = NoteName;
			FilePathArray[index] = cursor.getString(2);
			NoteBelongIdArray[index] = cursor.getInt(4);
			NoteTypeArray[index] = cursor.getInt(5);
			index++;
		}
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listview_noteitem, cursor, new String[] {
						NOTE_TYPEICON, NOTE_FILENAME, NOTE_TIME }, new int[] {
						R.id.NoteImageView, R.id.NoteName, R.id.NoteTime });
		NoteList.setAdapter(adapter);

		DBManager.close();
	}

	private void SetDateListView(String Select){
		// TODO Auto-generated method stub
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();
		Cursor[] TotalCursor = new Cursor[2];

		String Time = EventTime[0] + LessThanTen(EventTime[1])
				+ LessThanTen(EventTime[2]);
		String TimeSelect = Select + " AND " + NOTE_TIME + " = '" + Time + "'";

		cursor = db.query(TABLE_NAME_NoteInformation, new String[] { _ID,
				NOTE_FILENAME, CONTENT, NOTE_TIME, BELONG_CLASS_ID, NOTE_TYPE,
				NOTE_TYPEICON }, TimeSelect, null, null, null, null);

		int index = 0;
		while(cursor.moveToNext()){
			NoteName = cursor.getString(1);
			NoteNameArray[index] = NoteName;
			FilePathArray[index] = cursor.getString(2);
			NoteBelongIdArray[index] = cursor.getInt(4);
			NoteTypeArray[index] = cursor.getInt(5);
			index++;
		}
		TotalCursor[0] = cursor;

		String TimeSelect_Not = Select + " AND " + NOTE_TIME + " != '" + Time
				+ "'";
		cursor = db.query(TABLE_NAME_NoteInformation, new String[] { _ID,
				NOTE_FILENAME, CONTENT, NOTE_TIME, BELONG_CLASS_ID, NOTE_TYPE,
				NOTE_TYPEICON }, TimeSelect_Not, null, null, null, NOTE_TIME
				+ " ASC");

		while(cursor.moveToNext()){
			NoteName = cursor.getString(1);
			NoteNameArray[index] = NoteName;
			FilePathArray[index] = cursor.getString(2);
			NoteBelongIdArray[index] = cursor.getInt(4);
			NoteTypeArray[index] = cursor.getInt(5);
			index++;
		}
		TotalCursor[1] = cursor;

		Cursor MergeAllCursor = new MergeCursor(TotalCursor);

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listview_noteitem, MergeAllCursor, new String[] {
						NOTE_TYPEICON, NOTE_FILENAME, NOTE_TIME }, new int[] {
						R.id.NoteImageView, R.id.NoteName, R.id.NoteTime });

		NoteList.setAdapter(adapter);

		DBManager.close();
	}

	public void SetClassIDMethod(int CN){
		ClassID = CN;
	}

	public void SetFromCalendar(int FC){
		FromCalendar = FC;
	}

	public void SetTodayDate(int[] Date){
		for(int i = 0; i < 3; i++){
			EventTime[i] = Date[i];
		}
	}

	private void SelectList(final int position){
		final String[] ListItemName = { "修改", "分享", "刪除"};
		Builder ListAlertDialog = new AlertDialog.Builder(this.getActivity());
		ListAlertDialog.setTitle("請選擇")
				.setItems(ListItemName, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which){
						// TODO Auto-generated method stub
						if(NoteTypeArray[position] == 0){// Text content
							switch (which){
							case 0:
								Edit(position);
								break;
							case 1:
								//*********************************
								break;
							case 2:
								Delete(position);
								break;
							}
						}else if(NoteTypeArray[position] == 1){// Photo content
							switch (which){
							case 0:
								EditMediaNoteTitle(position);
								break;
							case 1:
								//*********************************
								mCallback.ShareNote_TypePhoto(getActivity(), NoteBelongIdArray[position], FilePathArray[position], NoteTypeArray[position]);
								break;
							case 2:
								DeleteMedia(position);
								break;
							}
						}else if(NoteTypeArray[position] == 2){// Voice content
							switch (which){
							case 0:
								EditMediaNoteTitle(position);
								break;
							case 1:
								//*********************************
								break;
							case 2:
								DeleteMedia(position);
								break;
							}
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which){
						// TODO Auto-generated method stub

					}
				}).show();
	}

	private void Edit(int position){
		Intent intent = new Intent();
		intent.putExtra("PageType", 1);
		intent.putExtra("NoteTitle", NoteNameArray[position]);
		intent.putExtra("BelongClassID", NoteBelongIdArray[position]);
		intent.setClass(getActivity(), NoteInsertPageActivity.class);
		startActivity(intent);
		this.getActivity().finish();
	}

	private void Delete(int position){
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getWritableDatabase();
		String DeleteNoteName = NOTE_FILENAME + " = '"
				+ NoteNameArray[position] + "'";
		db.delete(TABLE_NAME_NoteInformation, DeleteNoteName, null);
		SetListView(BelongClass_SELECT);
		DBManager.close();
	}

	private void EditMediaNoteTitle(final int position){
		// 修改標題視窗
		final EditText edittext = new EditText(this.getActivity());
		new AlertDialog.Builder(this.getActivity()).setTitle("請輸入修改後標題")
				.setView(edittext)
				.setPositiveButton("確定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which){
						// TODO Auto-generated method stub
						DBManager = new DataBaseManager(getActivity());
						SQLiteDatabase db = DBManager.getWritableDatabase();
						// Log.i("EditMediaNoteTitle",
						// "NoteNameArray[position]:"+NoteNameArray[position]+"position:"+position);
						ContentValues content = new ContentValues();
						String Select = NOTE_FILENAME + " = '"
								+ NoteNameArray[position] + "'";
						content.put(NOTE_FILENAME, edittext.getText()
								.toString());
						db.update(TABLE_NAME_NoteInformation, content, Select,
								null);
						DBManager.close();

						// Reflash ListView
						if(FromCalendar == 0){
							SetListView(BelongClass_SELECT);
						}else if(FromCalendar == 1){
							SetDateListView(BelongClass_SELECT);
						}
					}
				}).setNegativeButton("取消", null).show();

	}

	private void DeleteMedia(int position){
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getWritableDatabase();
		String DeleteNoteName = NOTE_FILENAME + " = '"
				+ NoteNameArray[position] + "'";
		db.delete(TABLE_NAME_NoteInformation, DeleteNoteName, null);

		// 刪除檔案//
		File DeFile = new File(FilePathArray[position]);
		if(DeFile.exists()){
			DeFile.delete();
		}

		SetListView(BelongClass_SELECT);
		DBManager.close();
	}

	protected void OnDestory(){
		cursor.close();
		super.onDestroy();
	}

}
