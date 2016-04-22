package com.app.classlogin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import static com.app.classlogin.ClassDataStructure_ClassNote.*;
import static com.app.classlogin.ClassDataStructure_MainClassTime.*;
import static com.app.classlogin.ClassDataStructure_CalendarTimeInformation.*;


public class DataBaseManager extends SQLiteOpenHelper{
	
	private final static String DB_NAME = "Class.db";
	private final static int DB_VERSION = 1;

	public DataBaseManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String Creat_Table_Note = "CREATE TABLE " + TABLE_NAME_NoteInformation + " (" 
									+ _ID 				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
									+ NOTE_TIME 		+ " CHAR NOT NULL, "
									+ NOTE_TYPE 		+ " INTEGER NOT NULL, "
									+ NOTE_TYPEICON 	+ " INTEGER NOT NULL, "
									+ NOTE_FILENAME 	+ " CHAR NOT NULL, "
									+ BELONG_CLASS_ID 	+ " INTEGER NOT NULL, "
									+ CONTENT 			+ " CHAR);";
		db.execSQL(Creat_Table_Note);
		
		final String Creat_Table_Time = "CREATE TABLE " + TABLE_NAME_TimeInformation + " (" 
									+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
									+ CLASS_SERIAL 	 + " INTEGER NOT NULL, "
									+ CLASS_NAME 	 + " CHAR NOT NULL, "
									+ WEEKS 		 + " CHAR NOT NULL, "
									+ START_DAY 	 + " DATE NOT NULL, "
									+ FRAGMENT_COUNT + " INTEGER NOT NULL, "
									+ DAY_OF_WEEK_1  + " CHAR NOT NULL, "
									+ DAY_OF_WEEK_2  + " CHAR , "
									+ DAY_OF_WEEK_3  + " CHAR , "
									+ DAY_OF_WEEK_4  + " CHAR , "
									+ DAY_OF_WEEK_5  + " CHAR , "
									+ DAY_OF_WEEK_6  + " CHAR , "
									+ TIME_START_1 	 + " CHAR NOT NULL, "
									+ TIME_START_2 	 + " CHAR , "
									+ TIME_START_3	 + " CHAR , "
									+ TIME_START_4	 + " CHAR , "
									+ TIME_START_5	 + " CHAR , "
									+ TIME_START_6	 + " CHAR , "
									+ TIME_END_1	 + " CHAR NOT NULL, "
									+ TIME_END_2	 + " CHAR , "
									+ TIME_END_3	 + " CHAR , "
									+ TIME_END_4	 + " CHAR , "
									+ TIME_END_5	 + " CHAR , "
									+ TIME_END_6	 + " CHAR , "
									+ END_DAY 		 + " DATE NOT NULL);";
		db.execSQL(Creat_Table_Time);
		
		final String Creat_Table_CTime = "CREATE TABLE "	 + TABLE_NAME_CalendarTimeInformation + " (" 
									+ _ID 					 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
									+ FRAGMENT_DATE			 + " DATE NOT NULL, "
									+ STARTTIME				 + " CHAR NOT NULL, "
									+ ENDTIME				 + " CHAR NOT NULL, "
									+ TIME_BELONG_CLASS_NAME + " CHAR NOT NULL, "
									+ TIME_BELONG_CLASS_ID	 + " INTEGER NOT NULL);";
		db.execSQL(Creat_Table_CTime);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME_TimeInformation;
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

}
