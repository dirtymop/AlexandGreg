package com.example.dirtymop.myapplication.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.classes.RouteEntry;

import java.util.ArrayList;

/**
 * Created by lndsharkfury on 7/31/15.
 */
public class StoredRouteAdapter extends ArrayAdapter<RouteEntry> implements View.OnClickListener {

    private final Context context;
    private final ArrayList<RouteEntry> entries;

    // Constructor with no initial data
    public StoredRouteAdapter(Context context) {
        super(context, R.layout.stored_route_entry_layout);

        this.context = context;
        this.entries = null;
    }

    // Constructor with initial data
    public StoredRouteAdapter(Context context, ArrayList<RouteEntry> entries) {
        super(context, R.layout.stored_route_entry_layout, entries);

        this.context = context;
        this.entries = entries;
    }

    @Override
    public void onClick(View v) {

    }
}
