package com.fhh.phone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FengHaHa on 2017/12/21.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private List<CallRecordsFragment> mFragments;

    private List<String> titles;

    public PagerAdapter(FragmentManager fm, List<CallRecordsFragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
        titles = new ArrayList<>();
        titles.add("呼入电话");
        titles.add("呼出电话");
        titles.add("未接来电");
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
