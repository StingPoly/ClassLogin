package com.app.classlogin;
//******************************************************
import static com.app.classlogin.NetParameter.Sender_ID;
import static com.app.classlogin.NetParameter.Server_URL;


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
//*
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

//import com.google.android.gcm.GCMRegistrar;

//import com.google.android.gcm.demo.app.R;

//******************************************************
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ClassLoginActivity extends Activity {
	
	EditText ClassNumber;
	EditText ClassNumberPass;
	String CNT,CNPT,URL;
	StringBuffer content;
	String ClassListfilename = "Class_file";
	AsyncTask<Void, Void, Integer> Net_Asynctask;
	AsyncTask<Void, Void, Void> SQL_Asynctask;
	ClassPageAnalysis PageAnalysis;
	
	
	//******************************************************
	String E_s0, E_s1, E_s2, E_s3, E_s4, E_s5;
	TextView T3,T4;
	HttpResponse response4 = null;
	//FileInputStream classlist = null;
	FileOutputStream classlist_out = null;
	BufferedInputStream buffer = null;
	//******************************************************
	
	
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classloginactivity_menu);
		ClassNumber = (EditText)findViewById(R.id.ClassNumberET);
		ClassNumberPass = (EditText)findViewById(R.id.ClassNumberPET);
		Button Login = (Button)findViewById(R.id.LogintoTKU);
		Button test = (Button)findViewById(R.id.testbutton);
		T3 = (TextView)findViewById(R.id.textView3);
		T3.setAutoLinkMask(Linkify.ALL);

		//if have "classlist" file, don't open login's button
		if(!existfile(ClassListfilename)){
			try {
				classlist_out = openFileOutput(ClassListfilename, Context.MODE_APPEND);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Login.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CNT = ClassNumber.getText().toString();
				CNPT = ClassNumberPass.getText().toString();
				CNT = "499411725";
				CNPT = "589016";
				URL = URL_Encryption(CNT, CNPT);
				
				login(URL);
			}
		});
		
		test.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String ClassNumber = CNT;
				gcm_regid(ClassNumber);
				
			}
		});
		
	}
	
	protected String URL_Encryption(String cnt, String cnpt){
		String url = null;
		E_s0 = cnt;
		E_s1 = cnpt;
		E_s2 = E_s0.toString();
		Myencode myencode = new Myencode();
		Myencode myencode1 = new Myencode();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("HHmmss");
		Date date = new Date(System.currentTimeMillis());
		
		E_s3 = simpledateformat.format(date).toString();
		E_s4 = myencode.md5(E_s2, E_s3);
		E_s5 = myencode1.encode(E_s1.toString());
        url = new StringBuilder("http://sinfo.ais.tku.edu.tw/eMisAppWs/AppWebservice.asmx/StuEleList2XML?pStuNo=").append(E_s2).append("&pKey=").append(E_s3).append("&pChksum=").append(E_s4).append("&pPass=").append(E_s5).toString();
		
		return url;
	}
	
	
	protected void login(final String url){
		
		Net_Asynctask = new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				final int result = HtmlGetByGet(url);
				write_list_file(PageAnalysis.getlist(), ClassListfilename);
				readfile(ClassListfilename);
				
				return result;
			}
			
			protected void onPostExecute(Integer result) {
				if(result > 0){
					Intent intent = new Intent();
					intent.setClass(ClassLoginActivity.this, ClassListActivity.class);
					startActivity(intent);
				}
				Net_Asynctask = null;
            }
		};
		Net_Asynctask.execute(null, null, null);
	}
	
	protected void write_account_file(String account, String pass, String filename){
		
		try {
			FileOutputStream writer = openFileOutput(filename, Context.MODE_PRIVATE);
			writer.write((account+"\n"+pass+"\n").getBytes());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	protected void write_list_file(String inputdata, String filename){
		try {
			FileOutputStream writer = openFileOutput(filename, Context.MODE_PRIVATE);
			writer.write(inputdata.getBytes());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected void gcm_regid(final String Usernumber){
//		checkNotNull(Server_URL, "Server_URL");
//		checkNotNull(Sender_ID, "Sender_ID");
//
//		GCMRegistrar.checkDevice(this);
//		GCMRegistrar.checkManifest(this);
//
//		final String regId = GCMRegistrar.getRegistrationId(this);
//
//		//registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
//
//		if (regId.equals("")) {
//	    	// Automatically registers application on startup.
//	        GCMRegistrar.register(this, Sender_ID);
//
//        }else{
//		//while(!(regId.equals(""))){
//
//		//}
//
//        if(GCMRegistrar.isRegisteredOnServer(this)){
//            // Skips registration.
//
//        }else{
//
//        	final Context context = this;
//            SQL_Asynctask = new AsyncTask<Void, Void, Void>(){
//
//				@Override
//				protected Void doInBackground(Void... arg0) {
//					// TODO Auto-generated method stub
//
//		            boolean registered =
//		                    NetConnectMethod.Register(context, regId, Usernumber);
//
//					return null;
//				}
//
//				@Override
//                protected void onPostExecute(Void result) {
//					SQL_Asynctask = null;
//                }
//
//            };
//            SQL_Asynctask.execute(null, null, null);
//        }
//
//        }
//
		
		
		
		
		
		
	}
	
	
	private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
	
	public String readfile(String filename){
		int readed;
		String content = "";
		byte[] buff = new byte[256];
		
		try {
			FileInputStream reader = openFileInput(filename);
			while((readed = reader.read(buff)) != -1){
				content += new String(buff).trim();
			}
			reader.close();
			
			return content;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return content;
		
	}
	
	public boolean existfile(String filename){
		String path = this.getFilesDir().getPath()+"//";
		File file = new File(path + filename);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	public int HtmlGetByGet(String url){

		HttpClient client = new DefaultHttpClient();
		try {
			
	        HttpGet httpget = new HttpGet(url);
			HttpResponse response = client.execute(httpget);
			Log.e("URL_1", url);
        	HttpEntity resEntity = response.getEntity();
        	String Content = EntityUtils.toString(resEntity);
        	PageAnalysis = new ClassPageAnalysis(Content);
        	
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			 client.getConnectionManager().shutdown();
		}
		return PageAnalysis.type_or_quantity;
	}

}
