package com.example.dirtymop.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.RoutePlannerActivity;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.classes.RouteEntry;
import com.example.dirtymop.myapplication.fragments.StoredMapSelection;

import java.util.ArrayList;

/**
 * Created by lndsharkfury on 7/31/15.
 */
public class StoredRouteAdapter extends ArrayAdapter<HistoryTable> implements View.OnClickListener {

    private final Context context;
    private final ArrayList<HistoryTable> entries;
    private final StoredMapSelection fragment;

    // Constructor with no initial data
    public StoredRouteAdapter(StoredMapSelection fragment, Context context) {
        super(context, R.layout.stored_route_entry_layout);

        this.context = context;
        this.entries = null;
        this.fragment = fragment;
    }
    // Constructor with initial data
    public StoredRouteAdapter(StoredMapSelection fragment, Context context, ArrayList<HistoryTable> entries) {
        super(context, R.layout.stored_route_entry_layout, entries);

        this.context = context;
        this.entries = entries;
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        // Load the entry expansion fragment

        // this is the index of the entry to be saved
        int indexInAdapter = Integer.parseInt(((String[]) view.getTag())[0]);
        Log.d("adapter", "item clicked: " + indexInAdapter);

        // get the item and pass to the activity
        this.fragment.loadExpansion(entries.get(indexInAdapter).getFacebookID()); // NEEDS UNIQUE_ID
    }

    @Override
    public void add(HistoryTable object) {
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the entry layout.
        final View row = inflater.inflate(R.layout.stored_route_entry_layout, parent, false);

        // Inite each item
        TextView textView = (TextView) row.findViewById(R.id.aTextView);
        textView.setText(entries.get(position).getFacebookID());

        row.setTag(new String[]{((Integer) position).toString()});
        row.setOnClickListener(this);

        return row;//super.getView(position, convertView, parent);
    }
}
