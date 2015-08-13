package com.example.dirtymop.myapplication.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.classes.ContactsTable;
import com.example.dirtymop.myapplication.classes.DatabaseHelper;
import com.example.dirtymop.myapplication.classes.HistoryEntry;
import com.example.dirtymop.myapplication.adapters.HistoryEntryAdapter;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.classes.PreferencesTable;
import com.example.dirtymop.myapplication.interfaces.HistoryInteractionListener;
import com.google.android.gms.wearable.DataApi;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class History extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Interface object
    private HistoryInteractionListener listener;

    // Array adpater object
    private HistoryEntryAdapter adapter;

    // XML elements
    private ListView entries;



    // Creates new instance of constructor
    public static History newInstance(String param1, String param2) {
        History fragment = new History();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public History() {
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        Toast.makeText(getActivity().getApplicationContext(), "onCreateView", Toast.LENGTH_SHORT).show();

        // Initialize the ListView.
        entries = (ListView) view.findViewById(R.id.historyList);
        entries.setDivider(null);
        entries.setDividerHeight(0);

        //pull the data from the local db save into thehistorytable
        DatabaseHelper dbhelper= new DatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase mydb = dbhelper.databaseOpenOrCreate("local.db");
        dbhelper.createTables(mydb);
        ArrayList<HistoryTable> thehistorytable=dbhelper.getHistoryEntry(mydb);
        Log.d("HistoryFragment","Loading from local db");

        // Initialize adapter -- place the thehistorytable into it
        adapter = new HistoryEntryAdapter(getActivity().getApplicationContext(), thehistorytable);
        entries.setAdapter(adapter);
        Log.d("HistoryFragment", "The Job is in the hands of the adapter");

        // Return the view.
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (HistoryInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement HistoryInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }




    private class Loadahistoryentry extends AsyncTask<Integer, Integer, Integer>
    {   private Handler passentryout;
        private Context context;
        public Loadahistoryentry(Context context, Handler passentryout) {

            super();
            this.passentryout=passentryout;
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            Bundle b = new Bundle();
            Message m = new Message();

            // put data into the bundle
            // netx
            // put the  bundle into the mesage

            passentryout.sendMessage(m);

            return null;
        }




    }
}
