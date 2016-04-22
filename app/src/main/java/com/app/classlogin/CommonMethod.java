package com.app.classlogin;

public class CommonMethod {
	public static String LessThanTen(int LTT_Integer){
		String LTT;
		if(LTT_Integer < 10){
			LTT = "0"+LTT_Integer;
    	}else{
    		LTT = ""+LTT_Integer;
    	}
		return LTT;
	}
	
	public static String LessThanTen(Long LTT_Integer){
		String LTT;
		if(LTT_Integer < 10){
			LTT = "0"+LTT_Integer;
    	}else{
    		LTT = ""+LTT_Integer;
    	}
		return LTT;
	}
}
