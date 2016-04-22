package com.app.classlogin;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_MainClassTime.*;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.*;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;

public class MainClassCalendarInsertEventActivity extends Activity{

	private DataBaseManager DBManager = null;
	Cursor cursor;

	final int MAX_LINE = 5;

	int TimeFragment_count = 1, hourOfDay, minute, year, month, dayOfMonth,
			Weeks, WeekCount, page_type;
	int Save_StartDayOfWeek;
	int ClassID, CheckSD, CheckED;
	Button[] DayButton, StartTimeButton, EndTimeButton;
	Button AddButton, DeleteButton, WeekButton, StartDayButton;
	TextView ClassSerialTV, ClassNameTV, DayTV, StartDayTV, StarttimeTV,
			EndtimeTV, WeeksTV, AddTV;
	EditText ClassSerialET, ClassNameET;
	String[] DayOfWeek = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四",
			"星期五", "星期六" };
	String[] DayOfWeek_show = new String[] { "日", "一", "二", "三", "四", "五", "六" };
	String[] DayOfWeek_SQL = new String[5];
	boolean[][] DayOfWeek_tf = new boolean[5][7];
	int[] DOW_BINARY_ARRAY = new int[5];
	// boolean[] TestArray = new boolean[7];
	int[] EventTime = new int[3];
	Calendar calendar;

	/*
	 * 1 課程名稱(TV) 課程名稱(ET) 2 開始日期(TV) 開始日期(BT) 3 星期(TV) 開始時間(TV) 結束時間(TV)
	 * 增減時段(TV) 4 星期(BT) 開始時間(BT) 結束時間(BT) 增減時段(BT) 5 課程週數(TV) 課程週數(BT) 6 取消(BT)
	 * 儲存(BT)
	 */
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_ui_set);
		this.DBManager = new DataBaseManager(this);
		Intent intent = getIntent();
		intent.getExtras();
		page_type = intent.getIntExtra("Page_Type", 0);// 0 = insert type, 1 =
														// edit type

		SetUI_ID();
		SetTime_Button();

		if(page_type == 0){// insert type
			Log.i("TypeTag", "insert type");
			InitBoolArray();
			EventTime = intent.getIntArrayExtra("EventTime");
			year = EventTime[0];
			month = EventTime[1];
			dayOfMonth = EventTime[2];
			calendar = Calendar.getInstance();
			hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

			Log.i("InsertTag", "Year:" + year + " Month:" + month
					+ " dayOfMonth:" + dayOfMonth);
			minute = calendar.get(Calendar.MINUTE);
			StartDayButton.setText(year + LessThanTen(month)
					+ LessThanTen(dayOfMonth));// +"/"

			/*
	         * 
	         * 
	         * 
	         * 
	         * */
			WeekButton.setText("18");
		}else{// edit type
				// Loading TimeFragment Information
			InitBoolArray();
			ClassNameET.setText(intent.getStringExtra("ClassName"));
			ClassID = intent.getIntExtra("ClassID", 0);

			// Select SQL to get class information
			SQLiteDatabase db = this.DBManager.getReadableDatabase();
			String SELECT = _ID + " ='" + ClassID + "'";
			Cursor cursor = db.query(TABLE_NAME_TimeInformation, new String[] {
					_ID, WEEKS, START_DAY, FRAGMENT_COUNT, DAY_OF_WEEK_1,
					TIME_START_1, TIME_END_1, DAY_OF_WEEK_2, TIME_START_2,
					TIME_END_2, DAY_OF_WEEK_3, TIME_START_3, TIME_END_3,
					DAY_OF_WEEK_4, TIME_START_4, TIME_END_4, DAY_OF_WEEK_5,
					TIME_START_5, TIME_END_5 }, SELECT, null, null, null, null);

			cursor.moveToFirst();

			// ****************************************//
			Analysis(cursor.getString(2));

			Log.i("EditTag", cursor.getString(1));
			WeekButton.setText(cursor.getInt(1) + "");
			StartDayButton.setText(cursor.getString(2));
			TimeFragment_count = Integer.valueOf(cursor.getString(3));
			Log.i("EditTag", "TFC:" + TimeFragment_count);

			DayOfWeek_SQL[0] = cursor.getString(4);
			CopyWeekArray(DayOfWeek_SQL[0], 0);
			DayButton[0].setText(cursor.getString(4));
			StartTimeButton[0].setText(cursor.getString(5));
			EndTimeButton[0].setText(cursor.getString(6));

			if(TimeFragment_count > 1){
				DayOfWeek_SQL[4] = cursor.getString(7);
				CopyWeekArray(DayOfWeek_SQL[4], 4);
				DayButton[4].setText(cursor.getString(7));
				StartTimeButton[4].setText(cursor.getString(8));
				EndTimeButton[4].setText(cursor.getString(9));
				DayButton[4].setVisibility(Button.VISIBLE);
				StartTimeButton[4].setVisibility(Button.VISIBLE);
				EndTimeButton[4].setVisibility(Button.VISIBLE);
				DeleteButton.setVisibility(Button.VISIBLE);

				if(TimeFragment_count > 2){
					DayOfWeek_SQL[1] = cursor.getString(10);
					CopyWeekArray(DayOfWeek_SQL[1], 1);
					DayButton[1].setText(cursor.getString(10));
					StartTimeButton[1].setText(cursor.getString(11));
					EndTimeButton[1].setText(cursor.getString(12));
					DayButton[1].setVisibility(Button.VISIBLE);
					StartTimeButton[1].setVisibility(Button.VISIBLE);
					EndTimeButton[1].setVisibility(Button.VISIBLE);
				}
				if(TimeFragment_count > 3){
					DayOfWeek_SQL[2] = cursor.getString(13);
					CopyWeekArray(DayOfWeek_SQL[2], 2);
					DayButton[2].setText(cursor.getString(13));
					StartTimeButton[2].setText(cursor.getString(14));
					EndTimeButton[2].setText(cursor.getString(15));
					DayButton[2].setVisibility(Button.VISIBLE);
					StartTimeButton[2].setVisibility(Button.VISIBLE);
					EndTimeButton[2].setVisibility(Button.VISIBLE);
				}
				if(TimeFragment_count > 4){
					DayOfWeek_SQL[3] = cursor.getString(16);
					CopyWeekArray(DayOfWeek_SQL[3], 3);
					DayButton[3].setText(cursor.getString(16));
					StartTimeButton[3].setText(cursor.getString(17));
					EndTimeButton[3].setText(cursor.getString(18));
					DayButton[3].setVisibility(Button.VISIBLE);
					StartTimeButton[3].setVisibility(Button.VISIBLE);
					EndTimeButton[3].setVisibility(Button.VISIBLE);
				}
			}

			cursor.close();

		}
		// final GridLayout grid = (GridLayout)findViewById(R.id.UIGrid);

		AddButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				if(TimeFragment_count < MAX_LINE){

					if(TimeFragment_count == 1){// For Delete Button
						DayButton[MAX_LINE - 1].setVisibility(Button.VISIBLE);
						StartTimeButton[MAX_LINE - 1]
								.setVisibility(Button.VISIBLE);
						EndTimeButton[MAX_LINE - 1]
								.setVisibility(Button.VISIBLE);
						DeleteButton.setVisibility(Button.VISIBLE);
					}else{
						DayButton[TimeFragment_count - 1]
								.setVisibility(Button.VISIBLE);
						StartTimeButton[TimeFragment_count - 1]
								.setVisibility(Button.VISIBLE);
						EndTimeButton[TimeFragment_count - 1]
								.setVisibility(Button.VISIBLE);
					}
					TimeFragment_count++;
				}
			}

		});
		DeleteButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				if(TimeFragment_count > 1){
					TimeFragment_count--;

					if(TimeFragment_count == 1){// For Delete Button
						DayButton[MAX_LINE - 1].setVisibility(Button.GONE);
						StartTimeButton[MAX_LINE - 1]
								.setVisibility(Button.GONE);
						EndTimeButton[MAX_LINE - 1].setVisibility(Button.GONE);
						DeleteButton.setVisibility(Button.GONE);
					}else{
						DayButton[TimeFragment_count - 1]
								.setVisibility(Button.GONE);
						StartTimeButton[TimeFragment_count - 1]
								.setVisibility(Button.GONE);
						EndTimeButton[TimeFragment_count - 1]
								.setVisibility(Button.GONE);
					}
				}
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.note_insert_page_menu, menu);
		MenuItem menuitem = menu.findItem(R.id.action_savenote);
		menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_savenote:
			if(ExceptionDetected()){
				AddClassOrEditClass();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void InitBoolArray(){
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 7; j++){
				DayOfWeek_tf[i][j] = false;
				DOW_BINARY_ARRAY[i] = 0;
				DayOfWeek_SQL[i] = "";
			}
		}
	}

	private void SetTime_Button(){

		WeekButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				final EditText edittext = new EditText(MainClassCalendarInsertEventActivity.this);
				edittext.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
				edittext.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
				InputFilter[] filter = new InputFilter[1];
				filter[0] = new InputFilter.LengthFilter(2);
				edittext.setFilters(filter);
				
				new AlertDialog.Builder(MainClassCalendarInsertEventActivity.this).setTitle("請輸入週數")
						.setView(edittext)
						.setPositiveButton("確定", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog, int which){
								// TODO Auto-generated method stub
								WeekButton.setText(edittext.getText().toString());
							}
						}).setNegativeButton("取消", null).show();
			}
		});

		StartTimeButton[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								StartTimeButton[0]
										.setText(LessThanTen(hourOfDay) + ":"
												+ LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		EndTimeButton[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								EndTimeButton[0].setText(LessThanTen(hourOfDay)
										+ ":" + LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		StartTimeButton[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								StartTimeButton[1]
										.setText(LessThanTen(hourOfDay) + ":"
												+ LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		EndTimeButton[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								EndTimeButton[1].setText(LessThanTen(hourOfDay)
										+ ":" + LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		StartTimeButton[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								StartTimeButton[2]
										.setText(LessThanTen(hourOfDay) + ":"
												+ LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		EndTimeButton[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								EndTimeButton[2].setText(LessThanTen(hourOfDay)
										+ ":" + LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		StartTimeButton[3].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								StartTimeButton[3]
										.setText(LessThanTen(hourOfDay) + ":"
												+ LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		EndTimeButton[3].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								EndTimeButton[3].setText(LessThanTen(hourOfDay)
										+ ":" + LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		StartTimeButton[4].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								StartTimeButton[4]
										.setText(LessThanTen(hourOfDay) + ":"
												+ LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		EndTimeButton[4].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new TimePickerDialog.OnTimeSetListener(){

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute){
								EndTimeButton[4].setText(LessThanTen(hourOfDay)
										+ ":" + LessThanTen(minute));
							}
						}, hourOfDay, minute, true);

				timePickerDialog.show();
			}

		});

		StartDayButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						MainClassCalendarInsertEventActivity.this,
						new DatePickerDialog.OnDateSetListener(){

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth){
								monthOfYear++;
								StartDayButton.setText(year
										+ LessThanTen(monthOfYear)
										+ LessThanTen(dayOfMonth));
								SetSelectTime(year, monthOfYear, dayOfMonth);
							}

							private void SetSelectTime(int Y, int MOY, int DOM){
								year = Y;
								month = MOY;
								dayOfMonth = DOM;
							}
						}, year, month - 1, dayOfMonth);
				datePickerDialog.show();
			}

		});

		DayButton[0].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				MultiChoice(DayButton[0], 0);
			}

		});

		DayButton[1].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				MultiChoice(DayButton[1], 1);
			}

		});

		DayButton[2].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				MultiChoice(DayButton[2], 2);
			}

		});

		DayButton[3].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				MultiChoice(DayButton[3], 3);
			}

		});

		DayButton[4].setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0){
				MultiChoice(DayButton[4], 4);
			}

		});
		
		

	}

	private void MultiChoice(final Button btn, final int Button_serial){
		new AlertDialog.Builder(MainClassCalendarInsertEventActivity.this)
				.setTitle("選擇日期")
				.setMultiChoiceItems(DayOfWeek, DayOfWeek_tf[Button_serial],
						new DialogInterface.OnMultiChoiceClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked){
								DayOfWeek_tf[Button_serial][which] = isChecked;
							}

						})
				.setPositiveButton("確定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which){
						int i;
						String s = "";
						for(i = 0; i < 7; i++){
							if(DayOfWeek_tf[Button_serial][i] == true){
								s = s + DayOfWeek_show[i];
							}
						}
						DayOfWeek_SQL[Button_serial] = s;
						Log.i("AlgTag", DayOfWeek_SQL[Button_serial] + "");
						if(s == ""){
							s = "未選擇日期";
						}
						btn.setText(s);
						// Log.i("AlgTag",
						// WeekBooleanToInt(DayOfWeek_tf[Button_serial])+"");
					}
				}).setNegativeButton("取消", null).show();
	}
	
	

	private void SetUI_ID(){

		ClassSerialET = (EditText) findViewById(R.id.IN_ClassSerial_ET);
		// ClassSerialET.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		ClassNameET = (EditText) findViewById(R.id.IN_ClassName_ET);

		DayButton = new Button[MAX_LINE];
		StartTimeButton = new Button[MAX_LINE];
		EndTimeButton = new Button[MAX_LINE];

		StartDayButton = (Button) findViewById(R.id.IN_StartDay_BT);

		DayButton[0] = (Button) findViewById(R.id.IN_Day_BT_0);
		StartTimeButton[0] = (Button) findViewById(R.id.IN_StartTime_BT_0);
		EndTimeButton[0] = (Button) findViewById(R.id.IN_EndTime_BT_0);

		DayButton[1] = (Button) findViewById(R.id.IN_Day_BT_1);
		StartTimeButton[1] = (Button) findViewById(R.id.IN_StartTime_BT_1);
		EndTimeButton[1] = (Button) findViewById(R.id.IN_EndTime_BT_1);

		DayButton[2] = (Button) findViewById(R.id.IN_Day_BT_2);
		StartTimeButton[2] = (Button) findViewById(R.id.IN_StartTime_BT_2);
		EndTimeButton[2] = (Button) findViewById(R.id.IN_EndTime_BT_2);

		DayButton[3] = (Button) findViewById(R.id.IN_Day_BT_3);
		StartTimeButton[3] = (Button) findViewById(R.id.IN_StartTime_BT_3);
		EndTimeButton[3] = (Button) findViewById(R.id.IN_EndTime_BT_3);

		DayButton[4] = (Button) findViewById(R.id.IN_Day_BT_4);
		StartTimeButton[4] = (Button) findViewById(R.id.IN_StartTime_BT_4);
		EndTimeButton[4] = (Button) findViewById(R.id.IN_EndTime_BT_4);

		WeekButton = (Button) findViewById(R.id.IN_Weeks_BT);

		AddButton = (Button) findViewById(R.id.IN_AddTime_BT);
		DeleteButton = (Button) findViewById(R.id.IN_DeleteTime_BT);

	}

	private void AddClassOrEditClass(){
		ExceptionDetected();

		SQLiteDatabase db = DBManager.getWritableDatabase();

		// Delete Time Fragment And Add New Time After
		String THIS_CLASS = TIME_BELONG_CLASS_ID + " = '" + ClassID + "'";
		if(page_type == 1){
			// Delete Time Fragment
			db.delete(TABLE_NAME_CalendarTimeInformation, THIS_CLASS, null);
		}

		ContentValues value = new ContentValues();

		value.put(CLASS_SERIAL, ClassSerialET.getText().toString());// *

		value.put(CLASS_NAME, ClassNameET.getText().toString());// *

		value.put(WEEKS, WeekButton.getText().toString());
		CheckSD = Integer.valueOf(StartDayButton.getText().toString());
		value.put(START_DAY, StartDayButton.getText().toString());// *
		value.put(FRAGMENT_COUNT, TimeFragment_count);

		value.put(DAY_OF_WEEK_1, DayOfWeek_SQL[0]);
		Log.i("SQLTAG", "S:" + DayOfWeek_SQL[0]);
		value.put(TIME_START_1, StartTimeButton[0].getText().toString());
		value.put(TIME_END_1, EndTimeButton[0].getText().toString());

		if(TimeFragment_count > 1){
			value.put(DAY_OF_WEEK_2, DayOfWeek_SQL[4]);
			value.put(TIME_START_2, StartTimeButton[4].getText().toString());
			value.put(TIME_END_2, EndTimeButton[4].getText().toString());
			if(TimeFragment_count > 2){
				value.put(DAY_OF_WEEK_3, DayOfWeek_SQL[1]);
				value.put(TIME_START_3, StartTimeButton[1].getText().toString());
				value.put(TIME_END_3, EndTimeButton[1].getText().toString());
			}
			if(TimeFragment_count > 3){
				value.put(DAY_OF_WEEK_4, DayOfWeek_SQL[2]);
				value.put(TIME_START_4, StartTimeButton[2].getText().toString());
				value.put(TIME_END_4, EndTimeButton[2].getText().toString());
			}
			if(TimeFragment_count > 4){
				value.put(DAY_OF_WEEK_5, DayOfWeek_SQL[3]);
				value.put(TIME_START_5, StartTimeButton[3].getText().toString());
				value.put(TIME_END_5, EndTimeButton[3].getText().toString());
			}
		}

		// Set Start Day
		calendar.set(year, month - 1, dayOfMonth);
		int increas_day = Integer.valueOf(WeekButton.getText().toString()) * 7;
		calendar.add(Calendar.DATE, increas_day);
		// Set End Day
		int StepYear = calendar.get(Calendar.YEAR);
		int StepMonth = calendar.get(Calendar.MONTH) + 1;
		int StepDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		CheckED = Integer.valueOf(StepYear + LessThanTen(StepMonth)
				+ LessThanTen(StepDayOfMonth));
		value.put(END_DAY, StepYear + LessThanTen(StepMonth)
				+ LessThanTen(StepDayOfMonth));

		if(page_type == 0){
			// Insert
			db.insert(TABLE_NAME_TimeInformation, null, value);
			Log.i("SQLTag", "insert");
		}else if(page_type == 1){
			// Update
			THIS_CLASS = _ID + " = '" + ClassID + "'";
			db.update(TABLE_NAME_TimeInformation, value, THIS_CLASS, null);
			Log.i("SQLTag", "Edit");
		}

		// 取得時間分段
		db = DBManager.getReadableDatabase();
		String GetID = CLASS_NAME + " ='" + ClassNameET.getText().toString()
				+ "'" + " AND " + START_DAY + " ='"
				+ StartDayButton.getText().toString() + "'";

		Cursor cursor = db.query(TABLE_NAME_TimeInformation,
				new String[] { _ID }, GetID, null, null, null, null);

		if(cursor.moveToFirst()){
			ClassID = cursor.getInt(0);
			Log.i("DoubleSelect",
					"ClassID:" + ClassID + " C.GC:" + cursor.getCount());
		}

		AddTimeFragment(db, 0);
		if(TimeFragment_count > 1){
			AddTimeFragment(db, 4);
			if(TimeFragment_count > 2){
				AddTimeFragment(db, 1);
			}
			if(TimeFragment_count > 3){
				AddTimeFragment(db, 2);
			}
			if(TimeFragment_count > 4){
				AddTimeFragment(db, 3);
			}
		}

		Intent intent = new Intent();
		intent.setClass(MainClassCalendarInsertEventActivity.this,
				MainClassCalendarActivity.class);
		startActivity(intent);
		finish();

	}

	public boolean onKeyDown(int KeyCode, KeyEvent event){

		if(KeyCode == KeyEvent.KEYCODE_BACK && page_type == 0){

			new AlertDialog.Builder(this)
					.setTitle("確定離開嗎?")
					.setPositiveButton("離開",
							new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which){
									finish();
								}
							}).setNegativeButton("取消", null).show();
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}

	// *****
	private void AddTimeFragment(SQLiteDatabase db, int cor){
		int StepYear, StepMonth, StepDayOfMonth;
		ContentValues time = new ContentValues();
		time.put(TIME_BELONG_CLASS_ID, ClassID);
		time.put(TIME_BELONG_CLASS_NAME, ClassNameET.getText().toString());
		time.put(STARTTIME, StartTimeButton[cor].getText().toString());
		time.put(ENDTIME, EndTimeButton[cor].getText().toString());
		calendar = Calendar.getInstance();

		// Set Start Day
		calendar.set(year, month - 1, dayOfMonth);
		Log.i("DateSelectTag", "Y:" + year + "M:" + month + "DOM:" + dayOfMonth);

		Save_StartDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		/* 0->SUN 1->MON 2->TUES 3->WED 4->THUR 5->FRI 6->SAT */
		Log.i("CheckTag_1", "SSD:" + Save_StartDayOfWeek);

		Weeks = Integer.valueOf(WeekButton.getText().toString());
		int DaySpace = 0, Seven;
		Log.i("CheckTag_2", "Weeks:" + Weeks);

		for(WeekCount = 0; WeekCount < Weeks; WeekCount++){
			Seven = 7;
			while(Seven > 0){
				// When DayOfWeek is Selected
				if(DayOfWeek_tf[cor][Save_StartDayOfWeek] == true){
					Log.i("ADayTag", "D_O_W:"
							+ DayOfWeek_show[Save_StartDayOfWeek]);

					calendar.add(Calendar.DATE, DaySpace);

					StepYear = calendar.get(Calendar.YEAR);
					StepMonth = calendar.get(Calendar.MONTH) + 1;
					StepDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

					Log.i("DateAddTag2", "Y:" + StepYear + "M:" + StepMonth
							+ "DOM:" + StepDayOfMonth);

					time.put(FRAGMENT_DATE, StepYear + LessThanTen(StepMonth)
							+ LessThanTen(StepDayOfMonth));

					// Insert
					db.insert(TABLE_NAME_CalendarTimeInformation, null, time);
					DaySpace = 1;

				}else{
					DaySpace++;
				}
				// Log.i("ADayTag", "DaySpace:" + DaySpace);

				if(Save_StartDayOfWeek > 5){
					Save_StartDayOfWeek = 0;
				}else{
					Save_StartDayOfWeek++;
				}

				Seven--;
			}

		}

	}

	public String LessThanTen(int LTT_Integer){
		String LTT;
		if(LTT_Integer < 10){
			LTT = "0" + LTT_Integer;
		}else{
			LTT = "" + LTT_Integer;
		}
		return LTT;
	}

	private boolean[] WeekIntToBoolean(int Binary_MAX_7){
		boolean[] week = new boolean[7];
		String s = "";
		if(Binary_MAX_7 > 127){
			Binary_MAX_7 = 127;
		}else if(Binary_MAX_7 < 0){
			Binary_MAX_7 = 0;
		}

		for(int i = 6; i >= 0; i--){
			if(Binary_MAX_7 >= pow(2, i)){
				week[i] = true;
				Binary_MAX_7 = Binary_MAX_7 - pow(2, i);

				s = s + "1";
			}else{
				week[i] = false;
				s = s + "0";
			}
		}
		Log.i("TEst", s);
		return week;
	}

	private int WeekBooleanToInt(boolean[] week){
		int Binary_MAX_7 = 0;

		for(int i = 0; i < 7; i++){
			if(week[i] == true){
				Binary_MAX_7 = Binary_MAX_7 + pow(2, i);
			}
		}
		return Binary_MAX_7;
	}

	private int pow(int x, int xc){
		int result = 1;
		if(xc == 0){
			result = 1;
		}else if(xc > 0){
			for(int i = 0; i < xc; i++){
				result = result * x;
			}
		}
		return result;
	}

	private void openDatabase(){
		DBManager = new DataBaseManager(this);
	}

	private void closeDatabase(){
		DBManager.close();
	}

	private void Analysis(String d){
		int y, m, dom;
		int idate = Integer.valueOf(d);

		y = idate / 10000;
		idate = idate - y * 10000;
		m = idate / 100;
		dom = idate - m * 100;
		year = y;
		month = m;
		dayOfMonth = dom;
	}

	private void CopyWeekArray(String WeekInformation, int cor){
		int N_week = 0;
		for(int w = 0; w < 7; w++){
			if(WeekInformation.indexOf(DayOfWeek_show[w]) != -1){
				N_week = N_week + pow(2, w);
			}
		}
		DayOfWeek_tf[cor] = WeekIntToBoolean(N_week);
		Log.i("CopyWeekArrayTag", "N_week COR:" + cor);
	}

	private boolean ExceptionDetected(){
		if(ClassNameET.getText().toString().equals("")){
			// have null class name
			new AlertDialog.Builder(this).setTitle("課程名稱不可空白")
					.setPositiveButton("確定", null).show();
			return false;
		}
		// **************************************************************************//
		/*
		 * if(ClassSerialET.getText().toString() == ""){ //have duplicate class
		 * name new AlertDialog.Builder(this).setTitle("")
		 * .setPositiveButton("確定", null) .show(); return false; }
		 */
		if(!OnlySelectNameDuplicate(ClassNameET.getText().toString())){
			// have duplicate class name
			new AlertDialog.Builder(this).setTitle("同一時段內已經有相同名稱的課程存在")
					.setPositiveButton("確定", null).show();
			return false;
		}

		if(!CheckStartAndEnd()){
			// have error class time
			new AlertDialog.Builder(this).setTitle("課程開始時間與結束時間不正確")
					.setPositiveButton("確定", null).show();
			return false;
		}

		if(!CheckDuplicateTime()){
			// have duplicate class time
			new AlertDialog.Builder(this).setTitle("課程時間與其他課程時間重疊")
					.setPositiveButton("確定", null).show();
			return false;
		}
		return true;
	}

	private boolean CheckDuplicateTime(){
		int StepYear, StepMonth, StepDayOfMonth, cor = 0;
		int weeks = Integer.valueOf(WeekButton.getText().toString());
		SQLiteDatabase db = DBManager.getReadableDatabase();

		calendar = Calendar.getInstance();
		// Set Start Day
		calendar.set(year, month - 1, dayOfMonth);

		Log.i("CheckTimeTag_1", "Weeks:" + weeks);

		for(cor = 0; cor < 5; cor++){
			int DaySpace = 0, Seven;
			for(WeekCount = 0; WeekCount < weeks; WeekCount++){
				Seven = 7;
				while(Seven > 0){
					// When DayOfWeek is Selected
					if(DayButton[cor].getVisibility() == 0
							&& DayOfWeek_tf[cor][Save_StartDayOfWeek] == true){
						Log.i("ADayTag", "D_O_W:"
								+ DayOfWeek_show[Save_StartDayOfWeek]);

						calendar.add(Calendar.DATE, DaySpace);

						StepYear = calendar.get(Calendar.YEAR);
						StepMonth = calendar.get(Calendar.MONTH) + 1;
						StepDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

						String ConditionDay = FRAGMENT_DATE
								+ " = '"
								+ (StepYear + LessThanTen(StepMonth) + LessThanTen(StepDayOfMonth))
								+ "'";
						Log.i("ConditionDay", ConditionDay);

						cursor = db.query(TABLE_NAME_CalendarTimeInformation,
								new String[] { _ID, STARTTIME, ENDTIME,
										FRAGMENT_DATE }, null, null, null,
								null, null);
						Log.i("GetCount()", cursor.getCount() + "");

						String StartTimeTag_S;
						String EndTimeTag_S;
						int StartTimeTag;
						int EndTimeTag;
						// cursor.moveToFirst();
						while(cursor.moveToNext()){
							StartTimeTag_S = cursor.getString(1);
							StartTimeTag = Removal(StartTimeTag_S);
							EndTimeTag_S = cursor.getString(2);
							EndTimeTag = Removal(EndTimeTag_S);

							String Stepday = cursor.getString(3);
							Log.i("CHTIMEARRAY", "STFA:" + StartTimeTag
									+ " ETFA:" + EndTimeTag + " FD:" + Stepday);
							int CheckStart = Removal(StartTimeButton[cor]
									.getText().toString());
							int CheckEnd = Removal(EndTimeButton[cor].getText()
									.toString());
							Log.i("CHTIMEARRAY", "CS:" + CheckStart + " CE:"
									+ CheckEnd);
							if((StartTimeTag < CheckStart && CheckStart < EndTimeTag)
									|| (StartTimeTag < CheckEnd && CheckEnd < EndTimeTag)
									|| (StartTimeTag > CheckStart && CheckEnd > EndTimeTag)){
								Log.i("(StartTimeTag < CheckStart )",
										"StartTimeTag:" + StartTimeTag
												+ " CheckStart:" + CheckStart);
								Log.i("(CheckStart < EndTimeTag)",
										"CheckStart:" + CheckStart
												+ " EndTimeTag:" + EndTimeTag);
								Log.i("(StartTimeTag < CheckEnd )",
										"StartTimeTag:" + StartTimeTag
												+ " CheckEnd:" + CheckEnd);
								Log.i("(CheckEnd < EndTimeTag)", "CheckEnd:"
										+ CheckEnd + " EndTimeTag:"
										+ EndTimeTag);
								Log.i("(StartTimeTag > CheckStart && CheckEnd > EndTimeTag)",
										"StartTimeTag:" + StartTimeTag
												+ " CheckStart:" + CheckStart
												+ "CheckEnd:" + " &&"
												+ CheckEnd + " EndTimeTag:"
												+ EndTimeTag);

								return false;
							}
						}
						Log.i("CHTIMEARRAY", "check");
						DaySpace = 1;
					}else{
						DaySpace++;
					}

					if(Save_StartDayOfWeek > 5){
						Save_StartDayOfWeek = 0;
					}else{
						Save_StartDayOfWeek++;
					}
					Seven--;
				}

			}
		}
		return true;
	}

	private int[] SameClassTimeStart = new int[5];
	private int[] SameClassTimeEnd = new int[5];

	private boolean CheckStartAndEnd(){
		for(int i = 0; i < 5; i++){
			// 0 = Visibility, 8 = GONE
			if(DayButton[i].getVisibility() == 0){
				if(Removal(StartTimeButton[i].getText().toString()) >= Removal(EndTimeButton[i]
						.getText().toString())){
					return false;
				}else{
					SameClassTimeStart[i] = Removal(StartTimeButton[i]
							.getText().toString());
					SameClassTimeEnd[i] = Removal(EndTimeButton[i].getText()
							.toString());
				}
			}else{
				SameClassTimeStart[i] = 0;
				SameClassTimeEnd[i] = 0;
			}
			// 跟自己的其他時間的重疊判斷 尚未完成

			if(true){
				DultiTimeCheck(
						Removal(StartTimeButton[i].getText().toString()),
						Removal(EndTimeButton[i].getText().toString()),
						SameClassTimeStart[i], SameClassTimeEnd[i]);
			}

		}
		return true;
	}

	private boolean OnlySelectNameDuplicate(String check_name){
		SQLiteDatabase db = DBManager.getReadableDatabase();
		// CheckSD CheckED
		String select = CLASS_NAME + " = '" + check_name + "'";
		Cursor cursor = db.query(TABLE_NAME_TimeInformation, new String[] {
				_ID, CLASS_NAME, START_DAY, END_DAY }, select, null, null,
				null, null);
		cursor.moveToFirst();
		while(cursor.moveToNext()){
			int StartDayTag, EndDayTag;
			StartDayTag = Integer.valueOf(cursor.getString(2));
			EndDayTag = Integer.valueOf(cursor.getString(3));

			if((StartDayTag < CheckSD && CheckSD < EndDayTag)
					|| (StartDayTag < CheckED && CheckED < EndDayTag)
					|| (StartDayTag > CheckSD && CheckED > EndDayTag)){
				Log.i("(StartTimeTag < CheckStart )", "StartTimeTag:"
						+ StartDayTag + " CheckStart:" + CheckSD);
				Log.i("(CheckStart < EndTimeTag)", "CheckStart:" + CheckSD
						+ " EndTimeTag:" + EndDayTag);
				Log.i("(StartTimeTag < CheckEnd )", "StartTimeTag:"
						+ StartDayTag + " CheckEnd:" + CheckED);
				Log.i("(CheckEnd < EndTimeTag)", "CheckEnd:" + CheckED
						+ " EndTimeTag:" + EndDayTag);
				Log.i("(StartTimeTag > CheckStart && CheckEnd > EndTimeTag)",
						"StartTimeTag:" + StartDayTag + " CheckStart:"
								+ CheckSD + "CheckEnd:" + " &&" + CheckED
								+ " EndTimeTag:" + EndDayTag);

				return false;
			}
		}
		return true;
	}

	private boolean DultiTimeCheck(int CheckStart, int CheckEnd,
			int StartDayTag, int EndDayTag){
		// 要測的 要測的 檢查標準 檢查標準
		if((StartDayTag < CheckStart && CheckStart < EndDayTag)
				|| (StartDayTag < CheckEnd && CheckEnd < EndDayTag)
				|| (StartDayTag > CheckStart && CheckEnd > EndDayTag)){
			return false;
		}
		return true;
	}

	private int Removal(String element){
		element = element.replace(":", "");
		Log.i("Removal", element);
		return Integer.valueOf(element);
	}
}
