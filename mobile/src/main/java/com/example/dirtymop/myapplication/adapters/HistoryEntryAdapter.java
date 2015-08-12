package com.example.dirtymop.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.classes.HistoryEntry;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.fragments.History;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class HistoryEntryAdapter extends ArrayAdapter<HistoryTable> implements View.OnClickListener {

    private final Context context;
    private final ArrayList<HistoryTable> entries;

    // Constructor
    public HistoryEntryAdapter(Context context, ArrayList<HistoryTable> entries) {
        super(context, R.layout.history_entry_layout, entries);

        // Initialize object attributes
        this.context = context;
        this.entries = entries;
        Log.d("listviewtest", "inside constructer");
    }

    @Override
    public void onClick(View v) {
        // Open up history view layout
        // Show details on specific entry
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Initialize layout inflater object.

//        Toast.makeText(getContext(), "getView", Toast.LENGTH_SHORT).show();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the entry layout.
        final View row = inflater.inflate(R.layout.history_entry_layout, parent, false);

        /* ACCESS ALL VIEW ELEMENTS HERE AND AFTERWARD */
        TextView distance = (TextView) row.findViewById(R.id.distanceHistoryView);
        TextView speed = (TextView) row.findViewById(R.id.speedHistoryView);
        TextView elevation =(TextView) row.findViewById(R.id.elevationHistoryView);
        TextView time =(TextView) row.findViewById(R.id.heartrateHistoryView);
        distance.setText("1 metric");
        speed.setText( "speak");
        elevation.setText(entries.get(position).getElevation());

        Log.d("listviewtest", entries.get(position).getFacebookID() + "inside adapter");



        // Link XML items to Java objects
      //  ProgressBar historyProgress = (ProgressBar) row.findViewById(R.id.historyProgress);
      //  historyProgress.setVisibility(View.VISIBLE); // Set progress bar to visible

        return row;
    }

    @Override
    public void add(HistoryTable object) {
        super.add(object);
    }
}
