package com.app.classlogin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.List;

/**
 * Created by stinglin on 2016/4/20.
 */
public class MainClassPageAdapter extends FragmentStatePagerAdapter{

    private Context context;
    List<Fragment> fragments;
    String title;

    private int[] iconimage = {
        R.drawable.ic_action_camera,
        R.drawable.ic_action_edit
    };

    public MainClassPageAdapter(FragmentManager fm, List<Fragment> fragments, Context context) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
//        this.title = fragments.title;
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

        Drawable image = context.getResources().getDrawable(iconimage[position]); //??Tabs??
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;

    }
}
