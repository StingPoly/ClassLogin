package com.app.classlogin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.util.Log;


public class ClassPageAnalysis {
	private int Index = 0, End_Index = 0, Part_Index = 0, Part_End_Index = 0;
	int type_or_quantity; // 0 = Error_Msg&no data, 1 = Class_content&have same number data
	
	String ClassCalender, ClassListfilename = "Class_file";
	public String[] ClassInformationStr;
	public String[][] ClassInformation;
	public String ClasslistS = "";
	
	public ClassPageAnalysis(String content){
		type_or_quantity = Identification_or_Count(content);
	}
	
	public String getlist(){
		return ClasslistS;
	}
	
	protected int Identification_or_Count(String content){
		if(content.indexOf("<Error_Msg>") != -1){
			//handle error message
			ClassInformationStr = content.split("<Error_Msg>")[1].split("</Error_Msg>");
			Log.e("ClassInformationStr", ClassInformationStr[0]);
			return 0;
		}else{
			
			//count class's quantity
			Index = content.indexOf("<stu_ele_table");
			End_Index = content.indexOf("</NewDataSet>");
			String stepinformation = content.substring(Index, End_Index);
			ClassInformationStr = stepinformation.split("<stu_ele_table ");
			ClassInformation = new String[ClassInformationStr.length][4];
			
			for(type_or_quantity = 0; type_or_quantity < ClassInformationStr.length - 1; type_or_quantity++){
				
				ClassInformation[type_or_quantity][0] = ClassInformationStr[type_or_quantity + 1].split("<ch_cos_name>")[1].split("</ch_cos_name>")[0];
				ClassInformation[type_or_quantity][1] = ClassInformationStr[type_or_quantity + 1].split("<time_plase>")[1].split("</time_plase>")[0];
				if(ClassInformationStr[type_or_quantity + 1].indexOf("<seat_no>") != -1){
					ClassInformation[type_or_quantity][2] = ClassInformationStr[type_or_quantity + 1].split("<seat_no>")[1].split("</seat_no>")[0];
				}else{
					ClassInformation[type_or_quantity][2] = "此課程無成績座號!";
				}
				ClassInformation[type_or_quantity][3] = ClassInformationStr[type_or_quantity + 1].split("<teach_name>")[1].split("</teach_name>")[0];
				
				ClasslistS += ClassInformation[type_or_quantity][0]+","+ClassInformation[type_or_quantity][1]
						 +","+ClassInformation[type_or_quantity][2]+","+ClassInformation[type_or_quantity][3]+"\n";
			}
			Log.e("ClassInformationStr", ClasslistS);
		
			
		}
		
		return type_or_quantity;
	}

	
	
}
