package com.example.dirtymop.myapplication.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.fragments.StoredMapSelection;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HistoryEntryAdapter extends ArrayAdapter<HistoryTable> implements View.OnClickListener {

    private final Context context;
    private final ArrayList<HistoryTable> entries;
    private final StoredMapSelection fragment;
    // Constructor
    public HistoryEntryAdapter(StoredMapSelection fragment, Context context) {
        super(context, R.layout.stored_route_entry_layout);

        this.context = context;
        this.entries = null;
        this.fragment = fragment;
    }
    public HistoryEntryAdapter(StoredMapSelection fragment,Context context, ArrayList<HistoryTable> entries) {
        super(context, R.layout.history_entry_layout, entries);

        // Initialize object attributes
        this.context = context;
        this.entries = entries;
        this.fragment = fragment;
        Log.d("listviewtest", "inside constructer");
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
        TextView topspeed =(TextView) row.findViewById(R.id.topspeedHistoryView);
        TextView date= (TextView) row.findViewById(R.id.dateHistoryView);
        TextView time= (TextView) row.findViewById(R.id.timeHistoryView);
        ImageView mapSnapshot = (ImageView) row.findViewById(R.id.mapSnapshot);

        // Analyze data
        float top_speed = 0;
        float avg_speed = 0;
        double avg_elevation = 0.0;



        String parts1[] = entries.get(position).getAvgspeed().split(";");
        Log.d("entry", parts1.toString());
        int I=0;
        if (parts1 != null)
        {
            while(I<parts1.length)
            {
                String sValue = parts1[I];
                float dValue = Float.parseFloat(sValue);

                // Sum the speeds.
                avg_speed = avg_speed + dValue;
                // Get maximum speed.
                if (I == 0) top_speed = dValue;
                else
                    if (dValue > top_speed) top_speed = dValue;

                I++;
            }

            // Average the speed.
            avg_speed = avg_speed/I;
        }

        String parts2[] = entries.get(position).getElevation().split(";");
        Log.d("entry", parts2.toString());
        I=0;
        if (parts2 != null)
        {
            while(I<parts2.length)
            {
                String sValue = parts2[I];
                Double dValue = Double.parseDouble(sValue);

                // Sum the speeds.
                avg_elevation = avg_elevation + dValue;

                I++;
            }

            // Average the speed.
            avg_elevation = avg_elevation/I;
        }

        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(entries.get(position).getTime()));

        topspeed.setText(Float.toString(avg_speed));//String.format("%.2f", Float.toString(avg_speed)));//entries.get(position).getTop_speed()
        date.setText(entries.get(position).getDate());
        time.setText(formatter.format(calendar.getTime()));//entries.get(position).getTime());
        distance.setText(entries.get(position).getDistance());
        speed.setText(Float.toString(avg_speed));//String.format("%.2f", Float.toString(avg_speed)));//entries.get(position).getAvgspeed()
        elevation.setText(Double.toString(avg_elevation));//String.format("%.2f", Double.toString(avg_speed)));//entries.get(position).getElevation()
        mapSnapshot.setImageBitmap(StringToBitMap(entries.get(position).getIdentify()));

        row.setTag(new String[]{((Integer) position).toString()});
        row.setOnClickListener(this);

        Log.d("listviewtest", entries.get(position).getFacebookID() + "inside adapter");

        return row;
    }

    @Override
    public void onClick(View view) {
        // Load the entry expansion fragment

        // this is the index of the entry to be saved
        int indexInAdapter = Integer.parseInt(((String[]) view.getTag())[0]);
        Log.d("adapter", "item clicked: " + indexInAdapter);

        // get the item and pass to the activity
        this.fragment.loadExpansion(entries.get(indexInAdapter).getIdentify()); // NEEDS UNIQUE_ID
    }
    @Override
    public void add(HistoryTable object) {
        super.add(object);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            encodeByte = null;
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}



// --------------------------------------
//public class StoredRouteAdapter extends ArrayAdapter<HistoryTable> implements View.OnClickListener {
//
//    private final Context context;
//    private final ArrayList<HistoryTable> entries;
//    private final StoredMapSelection fragment;
//
//    // Constructor with no initial data
//    public StoredRouteAdapter(StoredMapSelection fragment, Context context) {
//        super(context, R.layout.stored_route_entry_layout);
//
//        this.context = context;
//        this.entries = null;
//        this.fragment = fragment;
//    }
//    // Constructor with initial data
//    public StoredRouteAdapter(StoredMapSelection fragment, Context context, ArrayList<HistoryTable> entries) {
//        super(context, R.layout.stored_route_entry_layout, entries);
//
//        this.context = context;
//        this.entries = entries;
//        this.fragment = fragment;
//    }
//
//    @Override
//    public void onClick(View view) {
//        // Load the entry expansion fragment
//
//        // this is the index of the entry to be saved
//        int indexInAdapter = Integer.parseInt(((String[]) view.getTag())[0]);
//        Log.d("adapter", "item clicked: " + indexInAdapter);
//
//        // get the item and pass to the activity
//        this.fragment.loadExpansion(entries.get(indexInAdapter).getFacebookID()); // NEEDS UNIQUE_ID
//    }
//
//    @Override
//    public void add(HistoryTable object) {
//        super.add(object);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        // Inflate the entry layout.
//        final View row = inflater.inflate(R.layout.stored_route_entry_layout, parent, false);
//
//        // Inite each item
//        TextView textView = (TextView) row.findViewById(R.id.aTextView);
//        textView.setText(entries.get(position).getFacebookID());
//
//        row.setTag(new String[]{((Integer) position).toString()});
//        row.setOnClickListener(this);
//
//        return row;//super.getView(position, convertView, parent);
//    }
//}
