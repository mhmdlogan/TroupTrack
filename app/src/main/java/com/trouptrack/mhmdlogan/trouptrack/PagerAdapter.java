package com.trouptrack.mhmdlogan.trouptrack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by MhmdLoGaN on 24/03/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNoOfTabs;
    public PagerAdapter(FragmentManager fm,int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                FrndsTap1 frndsTap1 = new FrndsTap1();
                return frndsTap1;
            case 1:
                FrndsTap2 frndsTap2 = new FrndsTap2();
                return frndsTap2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
