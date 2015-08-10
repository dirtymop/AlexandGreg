package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeriesFormatter;
import com.example.dirtymop.myapplication.classes.AndroidWear;
import com.example.dirtymop.myapplication.services.LocationAndSensorService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HudActivity
        extends Activity
        implements ServiceConnection, OnMapReadyCallback {

    TextView latitude, longitude, accuracy, altitude, speed, acceleration;
    private int LOCATION_DISTANCE_REFRESH = 0;  // meters
    private int LOCATION_TIME_REFRESH = 500;    // milliseconds

    // Fragments
    FragmentManager fm;
    MapFragment mapFragment;
    private static final String TAG_FRAG_MAP = "map_fragment";

    // Google Maps
    GoogleMap googleMap;
    LatLng lastLatLng = null;

    // Service member variables
    LocationAndSensorService service;
    SharedPreferences serviceStatus;
    boolean bound = false;

    // Plotting
    private XYPlot plot;
    private SimpleXYSeries altitudeSeries, xAccelerationSeries, yAccelerationSeries, zAccelerationSeries;
    private double xHigh = 999.0;
    private double yHigh = 999.0;
    private double zHigh = 999.0;
    private int HISTORY_SIZE = 30;

    // Android Wear
    AndroidWear aw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make activitiy fullscreen.
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set view.
        setContentView(R.layout.activity_hud);

        // Create service
        createSerice();

        // Initialize TextView
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        accuracy = (TextView) findViewById(R.id.accuracy);
        altitude = (TextView) findViewById(R.id.altitude);
        speed = (TextView) findViewById(R.id.speed);
        acceleration = (TextView) findViewById(R.id.acceleration);
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // Plotting
//        altitudeSeries = new SimpleXYSeries("altitude");
        xAccelerationSeries = new SimpleXYSeries("x-axis");
        yAccelerationSeries = new SimpleXYSeries("y-axis");
        zAccelerationSeries = new SimpleXYSeries("z-axis");
        plot.setDomainBoundaries(-10, 10, BoundaryMode.AUTO);
        plot.setRangeBoundaries(-10, 10, BoundaryMode.AUTO);
        plot.setTitle("Acceleration Data");
        plot.addSeries(xAccelerationSeries, new LineAndPointFormatter(Color.BLACK, Color.RED, null, null));
        plot.addSeries(yAccelerationSeries, new LineAndPointFormatter(Color.BLACK, Color.WHITE, null, null));
        plot.addSeries(zAccelerationSeries, new LineAndPointFormatter(Color.BLACK, Color.BLUE, null, null));
        final PlotStatistics altiStats = new PlotStatistics(1000, false);
        plot.addListener(altiStats);

        Log.d("hud", "beginning map init...");
        // Initialize Google Maps fragment
        initMap();
        // Assign the current activity to receive callbacks from Google Maps.
        setMapCallbacks(this);
        Log.d("hud", "map init finished!");

        // Initialize Android Wear
        aw = new AndroidWear(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bound) this.unbindService(this); bound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("hud", "[onResume] service is started: " + serviceIsStarted());
//        while (!serviceIsStarted()) {
//            Log.d("hud", "help, I have fallen!");
//            try {
//                Thread.sleep(500);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        bindWithService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Destroy all the things...
        cleanup();
    }

    public void updateLocation(Bundle location) {
        latitude.setText("lat: " + Double.toString(location.getDouble("latitude")));
        longitude.setText("lon: " + Double.toString(location.getDouble("longitude")));
        altitude.setText("alt: " + Double.toString(location.getDouble("altitude")));
        accuracy.setText("acc: " + Float.toString(location.getFloat("accuracy")));
        speed.setText("speed: " + Float.toString(location.getFloat("speed")));

        // Add lat/lng to Google Maps
        updatePrimaryPath(new LatLng(location.getDouble("latitude"), location.getDouble("longitude")));
        centerMapOnLocation(new LatLng(location.getDouble("latitude"), location.getDouble("longitude")));

        aw.sendLatLng(new LatLng(location.getDouble("latitude"), location.getDouble("longitude")));


//        googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getDouble("latitude"), location.getDouble("longitude"))).title(getCurrentTime()));

//        // Plot altitude
//        if (altitudeSeries.size() > HISTORY_SIZE) altitudeSeries.removeFirst();
//        altitudeSeries.addLast(10, location.getDouble("accuracy"));
//        plot.redraw();
    }

    public void updateAccelerometer(Bundle data) {

        if (xHigh == 999.0 || xHigh < data.getDouble("x-axis")) xHigh = data.getDouble("x-axis");
        if (yHigh == 999.0 || yHigh < data.getDouble("y-axis")) yHigh = data.getDouble("y-axis");
        if (zHigh == 999.0 || zHigh < data.getDouble("z-axis")) zHigh = data.getDouble("z-axis");

        if (xHigh > 29.0 || yHigh > 29.0 || zHigh > 29.0) call("5404197390");

        acceleration.setText("Acceleration peaks:"
                + "\nX: " + xHigh
                + "\nY: " + yHigh
                + "\nZ: " + zHigh);

        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();

        if (xAccelerationSeries.size() > 50){
            xAccelerationSeries.removeFirst();
            yAccelerationSeries.removeFirst();
            zAccelerationSeries.removeFirst();
        }

        xAccelerationSeries.addLast(now, data.getDouble("x-axis"));
        yAccelerationSeries.addLast(now, data.getDouble("y-axis"));
        zAccelerationSeries.addLast(now, data.getDouble("z-axis"));
        plot.redraw();
    }

    private void call(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            if (number.length() == 10) callIntent.setData(Uri.parse("tel:" + number));
            else throw new NumberFormatException("Phone number must have 10 digits.");
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("hud", "Call failed: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            Log.e("hud", "Call failed: " + e.getMessage());
        }
    }

    /*
    * Service connection methods
    * */

    public void createSerice() {
        if (!serviceIsStarted()) this.startService(new Intent(HudActivity.this, LocationAndSensorService.class));
    }
    public void bindWithService() {
//        if (!serviceIsStarted()) this.startService(new Intent(HudActivity.this, LocationAndSensorService.class));
//        createSerice();
        Intent intent = new Intent(HudActivity.this, LocationAndSensorService.class);
        // Bind with the service
        this.bindService(intent, this, Context.BIND_AUTO_CREATE);
        // Change bound flag value;
        bound = true;
    }

    public boolean serviceIsStarted() {
        serviceStatus = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean flag = serviceStatus.contains("service_started") && serviceStatus.getBoolean("service_started", false);
        return flag;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Create a binder to the passed in service.
        LocationAndSensorService.LocalBinder binder = (LocationAndSensorService.LocalBinder) service;

        // Initialize local service placeholder with new instance.
        this.service = binder.getServiceInstance();

        // Assign the activity to send callbacks to.
        this.service.sendCallbacks(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
    /*
    * -------------------------
    * */

    /*
    * Google Maps
    * */
    // Initialize the Google Maps fragment.
    public void initMap() {
        // Initially set google maps object to null
        googleMap = null;

        // Initialize fragment manager and the map fragment
        fm = getFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentByTag(TAG_FRAG_MAP);

        // If map fragment doesn't already exist, create a new instance.
        if (mapFragment == null) {
            // Initialize map options
            GoogleMapOptions mapOptions = new GoogleMapOptions();
            mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                    .compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .tiltGesturesEnabled(true);

            // Generate new map instance
            mapFragment = MapFragment.newInstance(mapOptions);
        }

        // Populate the map frame with the map fragment.
        fm.beginTransaction().add(R.id.mapFrame, mapFragment).commit();
    }

    // Assign a callback for Google Maps.
    public void setMapCallbacks(OnMapReadyCallback cb) {
        mapFragment.getMapAsync(cb);
    }

    private void updatePrimaryPath(LatLng current) {
        PolylineOptions options = new PolylineOptions();
        options.color(Color.RED).width(5).visible(true);

        if (lastLatLng != null) {
            options.add(lastLatLng);
            options.add(current);
            googleMap.addPolyline(options);
        }

        lastLatLng = current;
    }

    private void centerMapOnLocation(LatLng loc) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("hud", "map is ready");

        // Set the local Google Map object.
        this.googleMap = googleMap;
    }
    /*
    * -------------------------
    * */

    // Gets the current time and date in a formatted string.
    public String getCurrentTime() {
        // Get current instance of the calendar.
        Calendar c = Calendar.getInstance();

        // Create a format for the date.
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");

        // Return the formatted string.
        return sdf.format(c.getTime());
    }

    // Method for tidying up onDestroy.
    public void cleanup() {
        mapFragment.onDestroy();
        service.onDestroy();
    }


}
