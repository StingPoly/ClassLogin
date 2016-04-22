package com.app.classlogin;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

public class MainClassCalendarTabListener implements TabListener{
	
	private Fragment mFragment;
	
	public MainClassCalendarTabListener(Fragment ft){
		mFragment = ft;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		ft.add(R.id.frameLayout, mFragment, null);
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		ft.remove(mFragment);
	}

}
