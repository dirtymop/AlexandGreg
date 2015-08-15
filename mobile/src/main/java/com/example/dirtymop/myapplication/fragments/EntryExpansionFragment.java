package com.example.dirtymop.myapplication.fragments;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.classes.DatabaseHelper;
import com.example.dirtymop.myapplication.classes.HistoryTable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EntryExpansionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EntryExpansionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntryExpansionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "unique_id";

    // TODO: Rename and change types of parameters
    private String unique_id;
    private static final String DB_FILENAME = "local.db";

    private OnFragmentInteractionListener mListener;

    // Plotting
    private XYPlot plot;
    private SimpleXYSeries altitudeSeries;//, xAccelerationSeries, yAccelerationSeries, zAccelerationSeries;
    private PlotStatistics altiStats;


    // TODO: Rename and change types and number of parameters
    public static EntryExpansionFragment newInstance(String unique_id) {
        EntryExpansionFragment fragment = new EntryExpansionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, unique_id);
        fragment.setArguments(args);
        return fragment;
    }

    public EntryExpansionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.unique_id = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_expansion, container, false);

        // Initialize database objects
        DatabaseHelper dbHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.databaseOpenOrCreate(DB_FILENAME);

        // Get all elements from database.
        HistoryTable entry = dbHelper.getSingleHistoryEntry(db, unique_id);

        if (entry != null) {
            // Initialze view members.
            TextView title = (TextView) view.findViewById(R.id.title);
            ImageView mapEntryImage = (ImageView) view.findViewById(R.id.mapEntryImage);

            // Create a DateFormatter object for displaying date in specified format.
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            // Create a calendar object that will convert the date and time value in milliseconds to date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(entry.getTime()));

            // Set the text and image elements.
            title.setText("Your route on " + entry.getDate() + " @ " + formatter.format(calendar.getTime()));
            mapEntryImage.setImageBitmap(StringToBitMap(entry.getIdentify()));

            // Plotting
            ArrayList<Double> altitude = new ArrayList<Double>();
            String parts[] = entry.getElevation().split(";");
            Log.d("entry", parts.toString());
            int I=0;
            if (parts != null)
            {
                while(I<parts.length)
                {
                    String sValue = parts[I];
                    Double dValue = Double.parseDouble(sValue);
                    altitude.add(dValue);
                    I++;
                }
            }

            if (altitude != null) {
                plot = (XYPlot) view.findViewById(R.id.altitudeXYPlot);
                altitudeSeries = new SimpleXYSeries("altitude");
                plot.setDomainBoundaries(0, altitude.size(), BoundaryMode.AUTO);
                plot.setRangeBoundaries(0, altitude.size(), BoundaryMode.AUTO);
                plot.setTitle("Altitude variation");
                plot.addSeries(altitudeSeries, new LineAndPointFormatter(Color.BLUE, Color.BLUE, Color.BLUE, null));
                altiStats = new PlotStatistics(1000, false);
                plot.addListener(altiStats);

                for (int i = 0; i < altitude.size(); i++) {
                    altitudeSeries.addLast(i, altitude.get(i));
                }
                plot.redraw();
            }
        }
        else {
            Toast.makeText(getActivity(), "no matching entries.", Toast.LENGTH_SHORT).show();
        }

        // Inflate the layout for this fragment
        return view;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
