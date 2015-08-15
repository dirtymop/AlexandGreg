package com.example.dirtymop.myapplication.fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.adapters.HistoryEntryAdapter;
import com.example.dirtymop.myapplication.classes.ContactsTable;
import com.example.dirtymop.myapplication.classes.DatabaseHelper;
import com.example.dirtymop.myapplication.classes.HistoryTable;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoredMapSelection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoredMapSelection extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // List view
    private ListView entriesListView;
    private FrameLayout expansionFrame;

    // List adapter
    private HistoryEntryAdapter adapter;

    // Elements to be loaded
    ArrayList<HistoryTable> entries;

    private static final String TAG_FRAG_HISTORY = "history_fragment";

    private android.app.FragmentManager fm;

    private Button selectMapButton;
    private TextView storedRouteTitlebar;

    // SQLite database
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private static final String DB_FILENAME = "local.db";

    // Expansion fragment
    EntryExpansionFragment eef;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoredMapSelection.
     */
    // TODO: Rename and change types and number of parameters
    public static StoredMapSelection newInstance(String param1, String param2) {
        StoredMapSelection fragment = new StoredMapSelection();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StoredMapSelection() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stored_map_selection, container, false);

        // Initialize the titlebar.
        storedRouteTitlebar = (TextView) view.findViewById(R.id.storedRouteTitlebar);
        storedRouteTitlebar.setText("Select a stored route...");
        storedRouteTitlebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "reloading entries...", Toast.LENGTH_SHORT).show();
//                getActivity().deleteDatabase("local.db");
                db = dbHelper.databaseOpenOrCreate(DB_FILENAME);
                loadEntries();
            }
        });

        // Initialize the ListView
        entriesListView = (ListView) view.findViewById(R.id.storageList);
        entriesListView.setVisibility(View.VISIBLE);
        expansionFrame = (FrameLayout) view.findViewById(R.id.expansionFrame);
        expansionFrame.setVisibility(View.GONE);


        // TODO: Populate the entries array list with all stored items
        // --> pull from local server.
        // --> set all items in a loop.


        // Pulls EContact from where it is in settings and saves it---------------------------------
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        ContactsTable EMS = new ContactsTable();
        EMS.setFacebookID("placeholder");
        EMS.setCustomerName("placeholder");
        EMS.setName(sharedPrefs.getString("nameofcontact", "Greg"));
        EMS.setNumber(sharedPrefs.getString("numberofcontact", "5404245176"));
        EMS.setEmail(sharedPrefs.getString("emailofcontact", "gdl@vt.edu"));

        // true or false settings check box
        Boolean watchison=sharedPrefs.getBoolean("watchonoroff",false);

        // SQLite
        dbHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
        db = dbHelper.databaseOpenOrCreate(DB_FILENAME);
        dbHelper.createTables(db);

        loadEntries();

        // SQLite
//        dbHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
//        db = dbHelper.databaseOpenOrCreate(DB_FILENAME);
//        dbHelper.createTables(db);
//
////        dbHelper.insertHistoryEntry(db, new HistoryTable(
////                "Facebookid1",
////                "CustomerName1",
////                "latslong1",
////                "date1",
////                "time1",
////                "elevation1",
////                "avgspeed1",
////                "distance1",
////                "identify1",
////                "markers1",
////                "timestarted1",
////                "topspeed1"
////        ));
//
//
//        entries = new ArrayList<HistoryTable>();
//        entries=dbHelper.getHistoryEntry(db);
//        db.close();
//
//        // Initialize the adapter
//        if (entries.size() != 0)
//            adapter = new HistoryEntryAdapter(this,getActivity().getApplicationContext(), entries);
//        else
//            adapter = new HistoryEntryAdapter(this,getActivity().getApplicationContext());
//
//        // Set the adapter for the ListView
//        entriesListView.setAdapter(adapter);

        //loads into local db
//        DatabaseHelper dbhelper= new DatabaseHelper(this.getActivity().getApplicationContext());
//        SQLiteDatabase mydb = dbhelper.databaseOpenOrCreate("local.db");
//        dbhelper.createTables(mydb);
//        dbhelper.insertContact(mydb,EMS);
//
//        //------------------------------------------------------------------------------------------
//
//
//
//
//
//
//        // SQLite
//        dbHelper = new DatabaseHelper(this.getActivity().getApplicationContext());
//        db = dbHelper.databaseOpenOrCreate(DB_FILENAME);
//        dbHelper.createTables(db);
//
////        dbHelper.insertHistoryEntry(db, new HistoryTable(
////                "Facebookid1",
////                "CustomerName1",
////                "latslong1",
////                "date1",
////                "time1",
////                "elevation1",
////                "avgspeed1",
////                "distance1",
////                "identify1",
////                "markers1",
////                "timestarted1",
////                "topspeed1"
////        ));
//
//
//        entries = new ArrayList<HistoryTable>();
//        entries=dbHelper.getHistoryEntry(db);
//        db.close();
//
//        // Initialize the adapter
//        if (entries.size() != 0)
//            adapter = new HistoryEntryAdapter(this,getActivity().getApplicationContext(), entries);
//        else
//            adapter = new HistoryEntryAdapter(this,getActivity().getApplicationContext());
//
//        // Set the adapter for the ListView
//        entriesListView.setAdapter(adapter);

        // Retun the inflated view
        return view;
    }

    public void loadEntries() {
        //
        entries = new ArrayList<HistoryTable>();
        entries=dbHelper.getHistoryEntry(db);
        db.close();

        // Initialize the adapter
        if (entries != null)
            adapter = new HistoryEntryAdapter(this,getActivity().getApplicationContext(), entries);
        else
            adapter = new HistoryEntryAdapter(this,getActivity().getApplicationContext());

        // Set the adapter for the ListView
        entriesListView.setAdapter(adapter);
    }

    public void loadExpansion(String unique_id) {

        storedRouteTitlebar.setText("Return to list...");
        storedRouteTitlebar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storedRouteTitlebar.setText("Select a stored route...");

                expansionFrame.setVisibility(View.GONE);
                entriesListView.setVisibility(View.VISIBLE);

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(eef).commit();

                storedRouteTitlebar.setOnClickListener(null);
            }
        });

        expansionFrame.setVisibility(View.VISIBLE);
        entriesListView.setVisibility(View.GONE);

        eef = EntryExpansionFragment.newInstance(unique_id);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.expansionFrame, eef).commit();
    }
}
