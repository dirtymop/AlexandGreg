package com.example.dirtymop.myapplication.fragments;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.RoutePlannerActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewMapSelection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewMapSelection
        extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private NewMapSelectionLisstener listener;

    // Layout Items
    private TextView goToHud, saveRoute, clearRoute, saveButton;
    private EditText markerMessage;
    private LinearLayout markerContentLayout;

    // Google Maps
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private HashMap<LatLng, String> markers;
    private Marker currentMarker;
    private static final String TAG_FRAG_MAP = "map_fragment";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewMapSelection.
     */
    // TODO: Rename and change types and number of parameters
    public static NewMapSelection newInstance(String param1, String param2) {
        NewMapSelection fragment = new NewMapSelection();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewMapSelection() {
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
    public void onMapLongClick(LatLng latLng) {
        markerContentLayout.setVisibility(View.VISIBLE);

        markerOnLocation(latLng, "...");
    }

    public interface NewMapSelectionLisstener {
        public void startHud();
        public void storeMarkers(HashMap<LatLng, String> markers);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (NewMapSelectionLisstener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MenuInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_map_selection, container, false);

        // Markers HashMap
        markers = new HashMap<LatLng, String>();

        // Button directing to the hud.
        goToHud = (TextView) view.findViewById(R.id.goToHud);
        goToHud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load markers to retained fragment on HUD launch.
                listener.storeMarkers(markers);

                // Start the HUD.
                listener.startHud();
            }
        });

        // Button saves all route content loaded on the screen.
        saveRoute = (TextView) view.findViewById(R.id.saveRoute);
        saveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load markers to retained fragment on HUD launch.
                listener.storeMarkers(markers);

                Toast.makeText(getActivity().getApplicationContext(), "Your map has been saved with " + markers.size() + " markers.", Toast.LENGTH_SHORT).show();
            }
        });

        // Button saves all route content loaded on the screen.
        clearRoute = (TextView) view.findViewById(R.id.clearRoute);
        clearRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete all markers
                flush();

                Toast.makeText(getActivity().getApplicationContext(), "Map is cleared.", Toast.LENGTH_SHORT).show();
            }
        });

        markerContentLayout = (LinearLayout) view.findViewById(R.id.markerContentLayout);
        markerContentLayout.setVisibility(View.GONE);

        markerMessage = (EditText) view.findViewById(R.id.markerMessage);
        saveButton = (TextView) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save the text to the marker
                currentMarker.setTitle(markerMessage.getText().toString());
                currentMarker.showInfoWindow();

                // Save marker to the hashmap.
                markers.put(currentMarker.getPosition(), currentMarker.getTitle());

                // Clear the view.
                markerMessage.setText("");
                markerContentLayout.setVisibility(View.GONE);
            }
        });

//        if (savedInstanceState != null) {
//            //Every time during the recreate of the activity, the retainedFragment will be lost, so we need to reassign the retainedFragment
//            mapFragment = (SupportMapFragment) getFragmentManager().findFragmentByTag(TAG_FRAG_MAP);
//        }
//        else {
//            // Initialize the Map fragment.
//            initMap();
//        }

        // Initialize the Map fragment.
        initMap();
        // Google Maps will send callbacks to this fragment.
        setMapCallbacks(this);

        // Inflate the layout for this fragment
        return view;
    }

    // Clears all map elements.
    private void flush() {
        googleMap.clear();
        markers.clear();
    }


    /*
    * Google Maps API Methods
    *
    * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.setMyLocationEnabled(true);
    }

    // Initialize the Google Maps fragment.
    public void initMap() {
        // Initially set google maps object to null
        googleMap = null;

        // Initialize fragment manager and the map fragment
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentByTag(TAG_FRAG_MAP);

        // If map fragment doesn't already exist, create a new instance.
        if (mapFragment == null) {
            // Initialize map options
            GoogleMapOptions mapOptions = new GoogleMapOptions();
            mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                    .compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .tiltGesturesEnabled(true);

            // Generate new map instance
            mapFragment = SupportMapFragment.newInstance(mapOptions);
        }

        // Populate the map frame with the map fragment.
        fm.beginTransaction().add(R.id.mapSelectionLayout, mapFragment).commit();
    }

    // Place a marker on location.
    private void markerOnLocation(LatLng loc, String message) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(loc)
                .visible(true)
                .title(message);
        currentMarker = googleMap.addMarker(markerOptions);
        currentMarker.showInfoWindow();
    }

    // Assign a callback for Google Maps.
    public void setMapCallbacks(OnMapReadyCallback cb) {
        mapFragment.getMapAsync(cb);
    }
}
