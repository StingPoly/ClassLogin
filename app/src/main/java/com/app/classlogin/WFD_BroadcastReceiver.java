package com.app.classlogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WFD_BroadcastReceiver extends BroadcastReceiver{

	private WifiP2pManager manager;
	private Channel channel;
	private MainClassCalendarActivity activity;
		

	public WFD_BroadcastReceiver(WifiP2pManager manager, Channel channel,
			MainClassCalendarActivity activity){
		super();
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent){
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
			// 確認手機上WifiP2P是否開啟
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				activity.WifiP2pEnable(true);
			}else{
				activity.WifiP2pEnable(false);
				activity.ClearPeerData();
			}

		}else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
			Log.d(MainClassCalendarActivity.Tag, "WIFI_P2P_PEERS_CHANGED_ACTION");
			// 當探測到可用節點列表改變時會啟動
			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if(manager != null){
				manager.requestPeers(channel, (PeerListListener) activity);

			}

		}else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)){
			Log.d(MainClassCalendarActivity.Tag, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
			// Broadcast intent action indicating that the state of Wi-Fi p2p
			// connectivity has changed.
			// 當P2P的連線狀態改變時啟動
			if(manager == null){
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			Log.d(MainClassCalendarActivity.TestTag, "State:"+networkInfo.getState());
			
			
			if(networkInfo.isConnected()){
				Log.d(MainClassCalendarActivity.Tag, "isConnected()");
//				Log.d(MainClassCalendarActivity.TestTag, "WF:"+manager.toString());
				// we are connected with the other device, request connection
				// info to find group owner IP
				//activity have connectioninfo type
				
				///************************
				manager.requestConnectionInfo(channel, activity);
				manager.requestGroupInfo(channel, activity);
				//傳送資料及接收資料
			}else{
				// Disconnect
//				activity.ClearPeerData();

			}
		}else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)){
			Log.d(MainClassCalendarActivity.Tag, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
			// 更新自己的設備資訊
			activity.UpdateThisDevice((WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

		}else if(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)){
			
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 10000);
			
			
		    if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED){
		        // Wifi P2P discovery started.
		    	
		    	
		    	
		    }else{
		        // Wifi P2P discovery stopped.
		        // Do what you want to do when discovery stopped
		    }
			
			
			
		}

	}

}
