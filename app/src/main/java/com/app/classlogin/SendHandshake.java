package com.app.classlogin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SendHandshake extends IntentService{
	
	private static final int SOCKET_TIMEOUT = 5000;
  
	public static final String ACTION_SEND_HANDSHAKE = "SEND_HANDSHAKE";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
	public static final String EXTRAS_DEVICE_MAC = "device_mac";

	public SendHandshake(String name){
		super(name);
	}
	
	public SendHandshake() {
        super("SendHandshake");
    }
	
	@Override
	protected void onHandleIntent(Intent intent){
		
		if (intent.getAction().equals(ACTION_SEND_HANDSHAKE)) {
            
			String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            String MAC = intent.getExtras().getString(EXTRAS_DEVICE_MAC);
            
            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                OutputStream stream = socket.getOutputStream();
                MainClassCalendarActivity.SendMessage(MAC, stream);
                
            } catch (IOException e) {
            	Log.e(MainClassCalendarActivity.Tag, e.toString());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                        	Log.e(MainClassCalendarActivity.Tag, e.toString());
                        }
                    }
                }
            }
        }
	}
	
	
	

}
