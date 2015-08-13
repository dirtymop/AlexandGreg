package com.example.dirtymop.myapplication.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dirtymop.myapplication.R;
import com.example.dirtymop.myapplication.classes.AndroidWear;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class RetainedFragment extends Fragment {

    // Data to be preserved
    ArrayList<LatLng> route = new ArrayList<LatLng>();
    HashMap<String, String> hashRoute = new HashMap<String, String>();
    ArrayList<LatLng> markers = new ArrayList<LatLng>();
    String date = "";
    float speed = 0;
    float distance = 0;
    AndroidWear aw = null;
    int routeIndex = 0;


    public RetainedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }





    // Setter methods
    public void updateRoute(ArrayList<LatLng> route) {
        this.route = route;
//        for (LatLng loc : route) {
//            this.route.add(loc);
//        }
    }
    public void updateMarkers(ArrayList<LatLng> markers) {
        this.markers = markers;
//        for (LatLng loc : markers) {
//            this.markers.add(loc);
//        }
    }
    public void updateHashRoute(HashMap<String, String> hashRoute) {
        Iterator it = hashRoute.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            this.hashRoute.put(pair.getKey().toString(), pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
    public void setDate(String date) { this.date = date; }
    public void setAndroidWear(AndroidWear aw) { this.aw = aw; }
    public void setRouteIndex(int routeIndex) { this.routeIndex = routeIndex; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void setDistance(float distance) { this.distance = distance; }





    // Getter methods.
    public ArrayList<LatLng> getRoute() { return route; }
    public ArrayList<LatLng> getMarkers() { return markers; }
    public HashMap<String, String> getHashRoute() { return hashRoute; }
    public String getDate() { return date; }
    public AndroidWear getAndroidWear() { return this.aw; }
    public int getRouteIndex() { return this.routeIndex; }
    public float getSpeed() { return this.speed; }
    public float getDistance() { return this.distance; }

}
