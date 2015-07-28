package com.example.dirtymop.myapplication.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.fragments.History;

import java.util.ArrayList;


public class HistoryEntryAdapter extends ArrayAdapter<HistoryEntry> implements View.OnClickListener {

    private final Context context;
    private final ArrayList<HistoryEntry> entries;

    // Constructor
    public HistoryEntryAdapter(Context context, ArrayList<HistoryEntry> entries) {
        super(context, R.layout.history_entry_layout, entries);

        // Initialize object attributes
        this.context = context;
        this.entries = entries;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Initialize layout inflater object.
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the entry layout.
        final View row = inflater.inflate(R.layout.history_entry_layout, parent, false);

        return row;
    }
}
