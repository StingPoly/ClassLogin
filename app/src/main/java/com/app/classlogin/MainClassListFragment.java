package com.app.classlogin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
//import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.CalendarView.OnDateChangeListener;

import static com.app.classlogin.CommonMethod.LessThanTen;
import static com.app.classlogin.ClassDataStructure_ClassNote.*;
import static com.app.classlogin.ClassDataStructure_MainClassTime.*;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.*;

@SuppressLint("ResourceAsColor")
public class MainClassListFragment extends Fragment {
	
	ListView ClassListView;
	Button CompletButton, NowButton;
	List<Map<String, Object>> classlist;
	private DataBaseManager DBManager = null;
	String ClassName = "";
	String[] ClassNameArray;//Class MAX = 600
	int[] ClassID;
	String Today_step = "";
	Cursor cursor;
	
	@Override	
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		final String Today = Today_step;
		String SELECT = END_DAY + " >= " + Today;
		SetViewComponent(SELECT);
		DisplayMetrics metrics = new DisplayMetrics();
	    this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    NowButton.setWidth(metrics.widthPixels/2);
	    //NowButton.setBackgroundColor(R.color.SelectedButton);
	    //NowButton.setBackgroundResource(R.color.SelectedButton);
	    
	    //NowButton.setBackgroundResource(R.drawable.abc_tab_selected_holo);
//	    NowButton.setBackgroundResource(R.drawable.abc_tab_indicator_ab_holo);//???????U?I???|?{
	    //NowButton.setBackgroundResource(R.drawable.abc_tab_selected_focused_holo);//???e?????U?T?w?b?I?????A
	    //NowButton.setBackgroundResource(R.drawable.abc_tab_selected_pressed_holo);//???e?????U?T?w?b?I?????A
	    //NowButton.setBackgroundResource(R.drawable.abc_tab_unselected_pressed_holo);//???e???????U?T?w?b?I?????A
	    //NowButton.setTextColor(R.color.SelectedButtonText);
	    
	    NowButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String SELECT = END_DAY + " >= " + Today;
				SetViewComponent(SELECT);
			}
	    	
	    });
	    
	    CompletButton.setWidth(metrics.widthPixels/2);
	    CompletButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String SELECT = END_DAY + " < " + Today;
				SetViewComponent(SELECT);
			}
	    	
	    });
	    
	    ClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("BelongClassID", ClassID[position]);
				intent.setClass(getActivity(), ClassInformationPageActivity.class);
				startActivity(intent);
			}
		});
	    
	    ClassListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.i("ClickTag", "ClassNameArray["+position+"]:"+ClassNameArray[position]+" ID:"+id);
				SelectList(position);
				return true;
			}
	    	
		});
		
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View fragmentView = inflater.inflate(R.layout.classlistlayout, container, false);
		ClassListView = (ListView)fragmentView.findViewById(R.id.ClasslistView);
		CompletButton = (Button)fragmentView.findViewById(R.id.CompletClass);
		NowButton = (Button)fragmentView.findViewById(R.id.NowClass);
		
		Calendar calendar = Calendar.getInstance();
		Today_step = calendar.get(Calendar.YEAR) + LessThanTen(calendar.get(Calendar.MONTH) + 1) + LessThanTen(calendar.get(Calendar.DAY_OF_MONTH));
		SetViewComponent(null);
        return fragmentView;
    }
	
	private void SetViewComponent(String Select) {
		//Select SQL to get class information
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = this.DBManager.getReadableDatabase();
		cursor = db.query(	TABLE_NAME_TimeInformation, 
							new String[] { _ID, CLASS_NAME, WEEKS, START_DAY, FRAGMENT_COUNT
							, DAY_OF_WEEK_1, TIME_START_1, TIME_END_1}
							, Select, null, null, null, null);
		
		ClassNameArray = new String[cursor.getCount()];
		ClassID = new int[cursor.getCount()];
		int index = 0;
		while (cursor.moveToNext()) {
			ClassID[index] = cursor.getInt(0);
			ClassName = cursor.getString(1);
			ClassNameArray[index++] = ClassName;
		}
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_item_list,
									  cursor, new String[] { CLASS_NAME/*, WEEKS*/, TIME_START_1, TIME_END_1/*, DAY_OF_WEEK_1*/}, 
									  new int[] {R.id.ClassName/*, R.id.Weeks*/, R.id.ClassStartTime, R.id.ClassEndTime/*, R.id.ClassDayOfWeek*/});
		ClassListView.setAdapter(adapter);
		DBManager.close();
	}
	
	private void SelectList(final int position){
		final String[] ListItemName = {"????", "?R??"};
		Builder ListAlertDialog = new AlertDialog.Builder(this.getActivity());
		ListAlertDialog.setTitle("??????").setItems(ListItemName, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which){
				case 0:
					Edit(position);
					break;
				case 1:
					Delete(position);
					break;
				}
				
			}
		}).setNegativeButton("????", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
	}
	
	private void Edit(int position){//************
		//Enter edit page
		Intent intent = new Intent();
		intent.setClass(getActivity(), MainClassCalendarInsertEventActivity.class);
		intent.putExtra("ClassName", ClassNameArray[position]);
		intent.putExtra("ClassID", ClassID[position]);
		intent.putExtra("Page_Type", 1);
		startActivity(intent);
		getActivity().finish();
	}
	
	private void Delete(int position){
		this.DBManager = new DataBaseManager(this.getActivity());
		SQLiteDatabase db = DBManager.getWritableDatabase();
		
		String DeleteClassNameID = _ID + " = '" + ClassID[position] + "'";
		db.delete(TABLE_NAME_TimeInformation, DeleteClassNameID, null);
		
		String DeleteBelongClassID = BELONG_CLASS_ID + " = '" + ClassID[position] + "'";
		db.delete(TABLE_NAME_NoteInformation, DeleteBelongClassID, null);
		
		String DeleteTimeBelongName = TIME_BELONG_CLASS_ID + " = '" + ClassID[position] + "'";
		db.delete(TABLE_NAME_CalendarTimeInformation, DeleteTimeBelongName, null);
		
		SetViewComponent(null);
		
		DBManager.close();
	}
	
	protected void onDestory(){
		cursor.close();
	    super.onDestroy();
	}
	
}
