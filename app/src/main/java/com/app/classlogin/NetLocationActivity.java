package com.app.classlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class NetLocationActivity extends Activity implements LocationListener{
	
	private boolean Service_open = false;
	private String BestProvider = LocationManager.NETWORK_PROVIDER;

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.netlocationactivity);
		
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		if(status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			Service_open = true;
			LocationReflash();
		}else{
			//if doesn't open gps or network, open setting page
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	}
	
	protected void onResume(){
		super.onResume();
		if(Service_open){
			//服務提供者、更新頻率1000毫秒=1s、最短距離、地點改變時呼叫物件
			LM.requestLocationUpdates(BestProvider, 1000, 1, this);
		}
	}
	
	protected void onPause(){
		super.onPause();
		if(Service_open){
			LM.removeUpdates(this);
		}
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		GetLocation(location);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	LocationManager LM;
	private void LocationReflash(){
		LM = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location location = LM.getLastKnownLocation(BestProvider);
		GetLocation(location);
	}
	
	private void GetLocation(Location location){
		if(location != null){
			TextView LocationText = (TextView)findViewById(R.id.LocationView);
			
			Double longitude = location.getLongitude();	//經度
			Double latitude = location.getLatitude();	//緯度
			
			LocationText.setText(longitude+", "+latitude);
			
		}else{
			Toast.makeText(this, "無法定位", Toast.LENGTH_LONG).show();
		}
		
		
	}
	

}
