package com.example.dirtymop.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.dirtymop.myapplication.fragments.NewMapSelection;
import com.example.dirtymop.myapplication.fragments.StoredMapSelection;

/**
 * Created by lndsharkfury on 7/31/15.
 */
public class MapSelectionTabAdapter extends FragmentPagerAdapter {

    // Constructor
    public MapSelectionTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return null;
    }

    // Get the number of items in the frament
    @Override
    public int getCount() {
        return 2;
    }
}
