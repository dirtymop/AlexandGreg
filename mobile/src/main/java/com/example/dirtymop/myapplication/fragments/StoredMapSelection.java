package com.example.dirtymop.myapplication.fragments;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.adapters.StoredRouteAdapter;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.interfaces.HistoryInteractionListener;

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
    private StoredRouteAdapter adapter;

    // Elements to be loaded
    ArrayList<HistoryTable> entries;


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

        // Initialize the ListView
        entriesListView = (ListView) view.findViewById(R.id.storageList);
        entriesListView.setVisibility(View.VISIBLE);
        expansionFrame = (FrameLayout) view.findViewById(R.id.expansionFrame);
        expansionFrame.setVisibility(View.GONE);


        // TODO: Populate the entries array list with all stored items
        // --> pull from local server.
        // --> set all items in a loop.

        entries = new ArrayList<HistoryTable>();
        entries.add(new HistoryTable("alex","1","1","1","1","1","1", "1", "1", "1", "1", "1"));
        entries.add(new HistoryTable("greg","2","2","2","2","2","2", "2", "2", "2", "2", "2"));

        // Initialize the adapter
        if (entries.size() != 0)
            adapter = new StoredRouteAdapter(this, getActivity().getApplicationContext(), entries);
        else
            adapter = new StoredRouteAdapter(this, getActivity().getApplicationContext());

        // Set the adapter for the ListView
        entriesListView.setAdapter(adapter);

        // Retun the inflated view
        return view;
    }

    public void loadExpansion(String unique_id) {
        expansionFrame.setVisibility(View.VISIBLE);
        entriesListView.setVisibility(View.GONE);

        EntryExpansionFragment eef = EntryExpansionFragment.newInstance(unique_id);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.expansionFrame, eef).commit();
    }
}
