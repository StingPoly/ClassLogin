package com.app.classlogin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ClassListActivity extends Activity {
	
	String ClassListfilename = "Class_file";
	String[] classlist;
	ArrayAdapter<String> listAdapter;
	ListView listview;
	Button clearbutton;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classtimelist);
		clearbutton = (Button)findViewById(R.id.clearbutton);
		listview = (ListView)findViewById(R.id.ClasslistView);
		readfile(ClassListfilename);
		
		clearbutton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listview.setAdapter(null);
				write_list_file("", ClassListfilename);
				
				Intent intent = new Intent();
				intent.setClass(ClassListActivity.this, ClassLoginActivity.class);
				startActivity(intent);
				ClassListActivity.this.finish();
			}
			
			
		});
		
		
	}
	
	protected void onDestory(){
		
		
		
		
	}
	
	public void readfile(String filename){
		int readed;
		String content = "";
		byte[] buff = new byte[256];
		
		try {
			FileInputStream reader = openFileInput(filename);
			while((readed = reader.read(buff)) != -1){
				content += new String(buff).trim();
			}
			Setlist(content);
			reader.close();
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
	
	protected void Setlist(String inputdata){
		String[] row;
		row = inputdata.split("\n");
		int i,j;
		//for(i = 0;i < row.length;i++){
			//String[] part;
			//part = row[i].split(",");
			/*for(j = 0;j < part.length;j++){
				
			}*/
		//}
		listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,row);
		listview.setAdapter(listAdapter);
		
	}

}
