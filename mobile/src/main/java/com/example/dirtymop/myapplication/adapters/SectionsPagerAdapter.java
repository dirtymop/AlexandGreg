package com.example.dirtymop.myapplication.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.fragments.NewMapSelection;
import com.example.dirtymop.myapplication.fragments.StoredMapSelection;

import java.util.Locale;

/**
 * Created by lndsharkfury on 7/31/15.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    String[] tabs;

    public SectionsPagerAdapter(FragmentManager fm, String... tabs) {
        super(fm);

        // Save the array of tab names.
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);

        switch (position) {
            case 0:
                return StoredMapSelection.newInstance("a", "z");
            case 1:
                return NewMapSelection.newInstance("a", "z");

        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return this.tabs.length;
    }

    // Get the title of the given page.
    @Override
    public CharSequence getPageTitle(int position) {
        if (position <= this.tabs.length) return this.tabs[position];
        else return null;
    }
}
