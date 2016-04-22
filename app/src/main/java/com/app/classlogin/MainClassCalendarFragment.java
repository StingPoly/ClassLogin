package com.app.classlogin;

//import android.app.Fragment;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.ENDTIME;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.FRAGMENT_DATE;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.STARTTIME;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.TABLE_NAME_CalendarTimeInformation;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.TIME_BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.TIME_BELONG_CLASS_NAME;
import static com.app.classlogin.ClassDataStructure_MainClassTime.CLASS_NAME;
import static com.app.classlogin.ClassDataStructure_MainClassTime.TABLE_NAME_TimeInformation;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
//import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainClassCalendarFragment extends Fragment {
	
	private DataBaseManager DBManager = null;
	public int[] EventTime = new int[3];
	Cursor cursor;
	CalendarView CV;
	ListView TodayEventListView;
	List<Map<String, Object>> classlist;
	int[] LinkID;
	String[] LinkName;
	String fragmentTitle;
	
	HandingDateSeletedListener mCallback;
	
	public interface HandingDateSeletedListener{
		public void OnDateSeletd(int year, int month, int DayOfMonth);
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View fragmentView = inflater.inflate(R.layout.mainclasscalendar_fragment_calendar, container, false);
		TodayEventListView = (ListView)fragmentView.findViewById(R.id.CalendarClassListView);
		
		Calendar calendar;
		calendar = Calendar.getInstance();
		
		GetEventList(calendar.get(Calendar.YEAR)+LessThanTen(calendar.get(Calendar.MONTH))+LessThanTen(calendar.get(Calendar.DAY_OF_MONTH)));
        return fragmentView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		GetUIComponent();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		SetDate(year, month, dayOfMonth);
		SetListView(year, month, dayOfMonth);
		
		CV.setOnDateChangeListener(new OnDateChangeListener(){

			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				// TODO Auto-generated method stub
				month++;
				mCallback.OnDateSeletd(year, month, dayOfMonth);
				SetListView(year, month, dayOfMonth);
				SetDate(year, month, dayOfMonth);
			}
		});
		
		
		TodayEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("BelongClassID", LinkID[position]);
				intent.putExtra("EventTime", EventTime);
				intent.putExtra("FromCalendar", 1);
				intent.setClass(getActivity(), ClassInformationPageActivity.class);
				startActivity(intent);
			}
		});
		
    }
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
            mCallback = (HandingDateSeletedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}
	
	private void GetUIComponent(){
		CV = (CalendarView)getView().findViewById(R.id.CalendarClassCalendarView);
		TodayEventListView = (ListView)getView().findViewById(R.id.CalendarClassListView);
	}
	
	//Get Today Event list, if no event then don't input any in listview
	private void GetEventList(String Today){
		
		String SELECT = FRAGMENT_DATE + " == " + Today;
		
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();
		cursor = db.query(TABLE_NAME_CalendarTimeInformation, 
								new String[] { _ID, FRAGMENT_DATE, STARTTIME, ENDTIME, TIME_BELONG_CLASS_ID, TIME_BELONG_CLASS_NAME}
								, SELECT, null, null, null, null);
		
		LinkID = new int[cursor.getCount()];
		LinkName = new String[cursor.getCount()];
		int index = 0;
		while(cursor.moveToNext()){	
			LinkName[index++] = cursor.getString(5);
			Log.i("LinkName", "LN:" + LinkName[(index - 1)] + " index:"+index);
		}
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_item_list,
				  cursor, new String[] { TIME_BELONG_CLASS_NAME, STARTTIME, ENDTIME}, 
				  new int[] {R.id.ClassName, R.id.ClassStartTime, R.id.ClassEndTime});
		
		TodayEventListView.setAdapter(adapter);
		DBManager.close();
		
		LinkIDFromLinkName();
	}
	

	private void SetListView(int year, int month, int dayOfMonth){
		
		String SELECT = FRAGMENT_DATE + " == " + year + LessThanTen(month) + LessThanTen(dayOfMonth);
		
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();
		cursor = db.query(TABLE_NAME_CalendarTimeInformation, 
								new String[] { _ID, FRAGMENT_DATE, STARTTIME, ENDTIME, TIME_BELONG_CLASS_ID, TIME_BELONG_CLASS_NAME}
								, SELECT, null, null, null, null);
		
		LinkID = new int[cursor.getCount()];
		LinkName = new String[cursor.getCount()];
		int index = 0;
		while(cursor.moveToNext()){	
			LinkName[index++] = cursor.getString(5);
			Log.i("LinkName", "LN:" + LinkName[(index - 1)] + " index:"+index);
		}
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_item_list,
				  cursor, new String[] { TIME_BELONG_CLASS_NAME, STARTTIME, ENDTIME}, 
				  new int[] {R.id.ClassName, R.id.ClassStartTime, R.id.ClassEndTime});
		
		TodayEventListView.setAdapter(adapter);
		
		DBManager.close();
		
		LinkIDFromLinkName();
	}
	

	private void LinkIDFromLinkName() {
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();
		
		for(int l = 0;l < LinkID.length;l++){
			String select = CLASS_NAME + " = '" + LinkName[l] + "'";
			cursor = db.query(TABLE_NAME_TimeInformation, new String[] {_ID}, select, null, null, null, null);
			cursor.moveToFirst();
			LinkID[l] = cursor.getInt(0);
			Log.i("LinkName", "LID:" + LinkID[l] + " l:"+l);
		}
		DBManager.close();
	}
	
	private void SetDate(int year, int month, int dayOfMonth) {
		EventTime[0] = year;
		EventTime[1] = month;
		EventTime[2] = dayOfMonth;
	}
	
	public String LessThanTen(int LTT_Integer){
		String LTT;
		if(LTT_Integer < 10){
			LTT = "0"+LTT_Integer;
    	}else{
    		LTT = ""+LTT_Integer;
    	}
		return LTT;
	}

	public String GetTitle(){
		return fragmentTitle;
	}
	
}
