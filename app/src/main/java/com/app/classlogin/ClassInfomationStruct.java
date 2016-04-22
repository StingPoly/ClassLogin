package com.app.classlogin;


public class ClassInfomationStruct{
	
	//TABLE_NAME_NoteInformation
	protected int Note_ID, Note_Type, Note_Typeicon, Belong_Class_ID;
	protected String Note_Time, Note_Filename, Content;
	
	//TABLE_NAME_TimeInformation
	protected int Class_ID, Fragment_Count;
	protected String Class_Name, Start_Day, Weeks, End_Day
				   , Day_Of_Week_1, Day_Of_Week_2, Day_Of_Week_3, Day_Of_Week_4, Day_Of_Week_5, Day_Of_Week_6
				   , Time_Start_1, Time_Start_2, Time_Start_3, Time_Start_4, Time_Start_5, Time_Start_6
				   , Time_End_1, Time_End_2, Time_End_3, Time_End_4, Time_End_5, Time_End_6;
	
	//TABLE_NAME_CalendarTimeInformation
	protected int FragmentTime_ID, Time_Belong_Class_ID;
	protected String Fragment_Date, FT_Start_Time, FT_End_Time, Time_Belong_Class_Name;

	public ClassInfomationStruct(){
		// TODO Auto-generated constructor stub
		
		
		
	}
	
	public void Set_NAME_NoteInformation(){
		
	}
	
	public void Set_NAME_TimeInformation(){
		
	}

	public void Set_NAME_CalendarTimeInformation(){
		
	}
	

}
