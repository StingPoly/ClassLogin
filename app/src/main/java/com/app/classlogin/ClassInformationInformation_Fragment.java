package com.app.classlogin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.CONTENT;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TIME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPE;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPEICON;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;
import static com.app.classlogin.ClassDataStructure_MainClassTime.*;

public class ClassInformationInformation_Fragment extends Fragment{
	
	private DataBaseManager DBManager = null;
	private String BelongClass_SELECT;
	private int ClassID;
	
	private TextView ShowClassSerial, ShowClassNameTV, ShowStartDayTV, ShowClassWeekTV, ShowTimeFragmentTV;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View fragmentView = inflater.inflate(R.layout.information_show_page, container, false);
		ShowClassSerial = (TextView) fragmentView.findViewById(R.id.ShowClassSerial);
		ShowClassNameTV = (TextView) fragmentView.findViewById(R.id.ShowClassNameTextView);
		ShowStartDayTV = (TextView) fragmentView.findViewById(R.id.ShowStartDayTextView);
		ShowClassWeekTV = (TextView) fragmentView.findViewById(R.id.ShowClassWeekTextView);
		ShowTimeFragmentTV = (TextView) fragmentView.findViewById(R.id.ShowTimeFragmentTextView);
        return fragmentView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();
		
		BelongClass_SELECT = "_id" + " = '" + ClassID + "'";
		
		Cursor cursor = db.query(TABLE_NAME_TimeInformation, new String[] { 
				CLASS_NAME, WEEKS, START_DAY, FRAGMENT_COUNT, 
				DAY_OF_WEEK_1/*4*/, DAY_OF_WEEK_2, DAY_OF_WEEK_3, DAY_OF_WEEK_4, DAY_OF_WEEK_5, DAY_OF_WEEK_6, 
				TIME_START_1/*10*/, TIME_START_2, TIME_START_3, TIME_START_4, TIME_START_5, TIME_START_6, 
				TIME_END_1/*16*/, TIME_END_2, TIME_END_3, TIME_END_4, TIME_END_5, TIME_END_6, CLASS_SERIAL, _ID}, BelongClass_SELECT, null, null, null, null);
		int ClassSerial = 0;
		while(cursor.moveToNext()){
			
			String ClassName = cursor.getString(0);
			String Weeks = cursor.getString(1);
			String StartDay = cursor.getString(2);
			StartDay = SericalChange(StartDay);
			int FragmentCount = cursor.getInt(3);
			ClassSerial = cursor.getInt(22);
			
			String id = cursor.getString(23);
			
			String FragmentString = "上課時間:\n";
			
			for(int count = 1;count <= FragmentCount;count++){//1-6
				int content = 3;
				//FragmentString += "星期";				//3	   +   1
				FragmentString += cursor.getString(content + count);
				FragmentString += "   ";
				FragmentString += cursor.getString(content + count + 6);
				FragmentString += "~";
				FragmentString += cursor.getString(content + count + 12);
				FragmentString += "\n";
			}
			ShowClassSerial.append(""+ClassSerial + _ID + id + " r:"+BelongClass_SELECT);
			ShowClassNameTV.append(ClassName);
			ShowStartDayTV.append(StartDay);
			ShowClassWeekTV.append(Weeks);ShowClassWeekTV.append("週");
			ShowTimeFragmentTV.setText(FragmentString);
		}
		
		String TestSelect = CLASS_SERIAL + " = '333'";
//		String TestSelect = CLASS_SERIAL + " = '" + ClassSerial + "'";
		
		Cursor cursor2 = db.query(TABLE_NAME_TimeInformation, new String[] { _ID}, TestSelect, null, null, null, null);
		ShowTimeFragmentTV.append("\nrule:"+TestSelect+" count:"+cursor2.getCount());
		
		if(cursor2.moveToNext()){
			ShowTimeFragmentTV.append("\nC2:"+cursor2.getInt(0));
		}
		
		/*
		if(cursor2.moveToFirst()){
			ShowTimeFragmentTV.append("\nC2:"+cursor2.getInt(0));
		}*/
		
		
		/*
		;
		while(cursor2.moveToNext()){
			ShowTimeFragmentTV.append("\nC2:"+cursor2.getInt(0));
		}*/
		
		
		
		
		
		DBManager.close();
    }
	
	private String SericalChange(String startDay){
		String temp = startDay.substring(0, 4);
		temp += "/";temp += startDay.substring(4, 5);
		temp += "/";temp += startDay.substring(5);
		return temp;
	}

	public void SetClassIDMethod(int CN){
		ClassID = CN;
	}

}
