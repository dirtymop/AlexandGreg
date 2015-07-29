package com.example.dirtymop.myapplication.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        Toast.makeText(getContext(), "getView", Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the entry layout.
        final View row = inflater.inflate(R.layout.history_entry_layout, parent, false);

        // Link XML items to Java objects
        ProgressBar historyProgress = (ProgressBar) row.findViewById(R.id.historyProgress);
        historyProgress.setVisibility(View.VISIBLE); // Set progress bar to visible

        return row;
    }
}
