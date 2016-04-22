// Copyright 2011 Google Inc. All Rights Reserved.

package com.app.classlogin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {
	
	//傳送檔案用的類別，另外需要寫一個接收檔案的AsyncTask
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "SEND_NOTE";
    public static final String EXTRAS_FILE_COMEFROM = "file_comefrom";
    public static final String EXTRAS_CLASS_SERIAL = "class_serial";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_FILE_TYPE = "file_type";
    public static final String EXTRAS_FILE_NAME = "file_name";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    

    public FileTransferService(String name) {
        super(name);
        //Log.d(WifiP2pActivity.AppTag, " on FileTransferService 1");
    }

    public FileTransferService() {
        super("FileTransferService");
        //Log.d(WifiP2pActivity.AppTag, " on FileTransferService 2");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
//    	Log.d(WifiP2pActivity.AppTag, " onHandleIntent");
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
        	
        	String fileComefrom = intent.getExtras().getString(EXTRAS_FILE_COMEFROM);
        	int classSerial = intent.getExtras().getInt(EXTRAS_CLASS_SERIAL);
        	String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            int fileType = intent.getExtras().getInt(EXTRAS_FILE_TYPE);
            String fileName = intent.getExtras().getString(EXTRAS_FILE_NAME);
            
            Log.d(com.app.classlogin.MainClassCalendarActivity.Tag, "classSerial:" +classSerial);
            
            Log.d(com.app.classlogin.MainClassCalendarActivity.Tag, "File Uri:" +fileUri);
            
            
            //Notice! This host value will be null value in sometime.
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            
            try {
            	
//            	File transfile = new File(fileUri);
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(com.app.classlogin.MainClassCalendarActivity.Tag, "Client socket - " + socket.isConnected());
                
                
                
                
                DataOutputStream dstream = new DataOutputStream(socket.getOutputStream());
                
                
                dstream.writeInt(classSerial);
                dstream.writeInt(fileType);
                dstream.writeUTF(fileName);
                Log.d(com.app.classlogin.MainClassCalendarActivity.Tag, "T-URI: " + fileName);
                
//                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = getContentResolver();
                               
//                InputStream is = null;
                DataInputStream is = null;
                try {
//                    is = cr.openInputStream(Uri.parse(fileUri));
                	is = new DataInputStream(cr.openInputStream(Uri.parse(fileUri)));
                	
                    
                } catch (FileNotFoundException e){
                	Log.d(MainClassCalendarActivity.Tag, "FileNotFoundException"+e.toString());
                }
//                MainClassCalendarActivity.copyFile(is, stream);
                MainClassCalendarActivity.data_CopyFile(is, dstream);
                
                
                Log.d(com.app.classlogin.MainClassCalendarActivity.Tag, "Client IP:" + socket.getInetAddress());
                
            } catch (IOException e) {
            	Log.d(MainClassCalendarActivity.Tag, e.toString());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
    
}
