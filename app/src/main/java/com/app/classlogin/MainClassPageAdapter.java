package com.app.classlogin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by stinglin on 2016/4/20.
 */
public class MainClassPageAdapter extends FragmentStatePagerAdapter{

    private Context context;
    List<Fragment> fragments;
    String title;

    public MainClassPageAdapter(FragmentManager fm, List<Fragment> fragments, Context context) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.title = frag
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position){

        return title;
    }
}
