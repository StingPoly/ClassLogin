package com.app.classlogin;

import android.provider.BaseColumns;

public interface ClassDataStructure_MainClassTime extends BaseColumns{
	public static final String TABLE_NAME_TimeInformation = "ClassTimeInformation";
	
	public static final String CLASS_SERIAL = "serial";
	public static final String CLASS_NAME = "classname";
	public static final String WEEKS = "weeks";
	public static final String START_DAY = "startday";
	
	public static final String FRAGMENT_COUNT = "timefragment_count";
	
	public static final String DAY_OF_WEEK_1 = "dayofweek_1";//MAX 127, USE Binary TO KNOW DAY_OF_WEEK
	public static final String DAY_OF_WEEK_2 = "dayofweek_2";
	public static final String DAY_OF_WEEK_3 = "dayofweek_3";
	public static final String DAY_OF_WEEK_4 = "dayofweek_4";
	public static final String DAY_OF_WEEK_5 = "dayofweek_5";
	public static final String DAY_OF_WEEK_6 = "dayofweek_6";
	
	public static final String TIME_START_1 = "Time_Start_1";
	public static final String TIME_START_2 = "Time_Start_2";
	public static final String TIME_START_3 = "Time_Start_3";
	public static final String TIME_START_4 = "Time_Start_4";
	public static final String TIME_START_5 = "Time_Start_5";
	public static final String TIME_START_6 = "Time_Start_6";
	
	public static final String TIME_END_1 = "Time_End_1";
	public static final String TIME_END_2 = "Time_End_2";
	public static final String TIME_END_3 = "Time_End_3";
	public static final String TIME_END_4 = "Time_End_4";
	public static final String TIME_END_5 = "Time_End_5";
	public static final String TIME_END_6 = "Time_End_6";
	//x-113 z-472
	public static final String END_DAY = "End_Day";

}
