package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.dirtymop.myapplication.classes.DatabaseHelper;
import com.example.dirtymop.myapplication.services.LocationAndSensorService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    HashMap<String, String> route;
    int routeIndex;
    private final float ZOOM_LEVEL = 17;
    private final int ZOOM_DURATION = 1000; // milliseconds
    private Marker currentMarker = null;

    // Service member variables
    LocationAndSensorService service;
    SharedPreferences serviceStatus;
    boolean bound = false;

    // Plotting
    private XYPlot plot;
    private SimpleXYSeries altitudeSeries, xAccelerationSeries, yAccelerationSeries, zAccelerationSeries;

    // Sensor Algorythm variables
    private double xHigh = 999.0;
    private double yHigh = 999.0;
    private double zHigh = 999.0;
    private double xCurrent, yCurrent, zCurrent;
    private double xMobile, yMobile, zMobile;
    private double xWear, yWear, zWear;

    // Android Wear
    AndroidWear aw;
    private int count = 0;

    // SQLite database
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private static final String DB_FILENAME = "local.db";

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hud","received broadcast from service!");
            bindWithService();

        }
    };

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
        IntentFilter intentFilter = new IntentFilter("toActivity");
        registerReceiver(activityReceiver, intentFilter);
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

        // Create new hashmap for route
        route = new HashMap<String, String>();
        routeIndex = 0;
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
        if (serviceIsStarted()) bindWithService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Send route data to the local database.
        saveRouteToLocalDB(route);

        // Destroy all the things...
        cleanup();
    }

    public void updateLocation(Bundle location) {

        // Set as current location
        LatLng current = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));

        latitude.setText("lat: " + Double.toString(location.getDouble("latitude")));
        longitude.setText("lon: " + Double.toString(location.getDouble("longitude")));
        altitude.setText("alt: " + Double.toString(location.getDouble("altitude")));
        accuracy.setText("acc: " + Float.toString(location.getFloat("accuracy")));
        speed.setText("speed: " + Float.toString(location.getFloat("speed")));

        // Add lat/lng to Google Maps
        updatePrimaryPath(current);
        centerMapOnLocation(current);

        // Update marker to current location
        markerOnLocation(current);

        // Place a marker @ the starting location
//        if (routeIndex == 0) {
//            googleMap.addMarker(
//                    new MarkerOptions()
//                            .position(current)
//                            .title("Start: " + getCurrentTime()));
//        }

        // Save latitude and longitude to the map
        String locationString = Double.toString(current.latitude)
                        + ","
                        + Double.toString(current.longitude);
        route.put(Integer.toString(routeIndex), locationString);
        routeIndex++; // Increment the route index.

        Log.d("hud","updating location");

        aw.sendLatLng(current);

//        if (count == 2) {
//            Toast.makeText(this, "sending coordinates to wear!", Toast.LENGTH_SHORT).show();
//            count = 0;
//            // Send Lat/Lng to AW.
//            aw.sendLatLng(current);
//        }
//        else count++;
    }

    public boolean isOverThreshold(double[] mobile, double[] wear) {
        if ((mobile[0] > 11.0 || mobile[1] > 11.0 || mobile[2] > 11.0) && (wear[0] > 11.0 || wear[1] > 11.0 || wear[2] > 11.0))
            return true;
//        if ((mobile[0] > 29.0 || mobile[1] > 29.0 || mobile[2] > 29.0))
//            return true;
        else return false;
    }

    public void updateAccelerometer(String type, Bundle data) {

        // Set the current values
        xCurrent = data.getDouble("x-axis");
        yCurrent = data.getDouble("y-axis");
        zCurrent = data.getDouble("z-axis");

        // Set variables depending on which type was called
        if (type.equals("mobile")) {
            xMobile = xCurrent;
            yMobile = yCurrent;
            zMobile = zCurrent;

            // Set textview text.
            acceleration.setText("Acceleration peaks:"
                    + "\nX: " + xHigh
                    + "\nY: " + yHigh
                    + "\nZ: " + zHigh);

            // Time for domain plot
            Calendar c = Calendar.getInstance();
            long now = c.getTimeInMillis();

            // Only plot 50 entries at a time.
            if (xAccelerationSeries.size() > 50){
                xAccelerationSeries.removeFirst();
                yAccelerationSeries.removeFirst();
                zAccelerationSeries.removeFirst();
            }

            // Add entry to each data series.
            xAccelerationSeries.addLast(now, data.getDouble("x-axis"));
            yAccelerationSeries.addLast(now, data.getDouble("y-axis"));
            zAccelerationSeries.addLast(now, data.getDouble("z-axis"));

            // Redraw the plot with the new data.
            plot.redraw();
        }
        if (type.equals("wear")) {
            xWear = xCurrent;
            yWear = yCurrent;
            zWear = zCurrent;
        }

        // Get peak values.
        if (xHigh == 999.0 || xHigh < data.getDouble("x-axis")) xHigh = xCurrent;
        if (yHigh == 999.0 || yHigh < data.getDouble("y-axis")) yHigh = yCurrent;
        if (zHigh == 999.0 || zHigh < data.getDouble("z-axis")) zHigh = zCurrent;

        // Threshold
        if (isOverThreshold(new double[] {xMobile, yMobile, zMobile}, new double[] {xWear, yWear, zWear})) {
            handleEmergency();
        }
    }

    // Handles all methods pertaining to emergency response.
    private void handleEmergency() {
        Toast.makeText(this, "EMERGENCY: calling, 5404197390", Toast.LENGTH_SHORT).show();

        // Ensure user can make phone calls.
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            call("5404197390");
        }
        else {
            Toast.makeText(this, "calling not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    // Call a designated phone number.
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
    *
    * */
    public void createSerice() {
        if (!serviceIsStarted()) this.startService(new Intent(HudActivity.this, LocationAndSensorService.class));
    }

    public void bindWithService() {
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

        Log.d("hud", "service is connected & callbacks are set!");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
    /*
    * -------------------------
    * */

    /*
    * Google Maps
    *
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM_LEVEL));
    }
    private void markerOnLocation(LatLng loc) {
        if (currentMarker != null)  {
            currentMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(loc)
                .visible(true)
                .title("hello world")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot));
        currentMarker = googleMap.addMarker(markerOptions);
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

    // Save route to local database
    public void saveRouteToLocalDB(HashMap<String, String> data) {
        // Instance of database helper class
        dbHelper = new DatabaseHelper(this);
        // Instance of local SQLite database
        db = dbHelper.databaseOpenOrCreate(DB_FILENAME);

        // TODO: put data into local DB here
        //
        // db.insertRoute(...)
        Log.d("hud", "saving route..." + "\nlocal DB: " + DB_FILENAME + "\ncontents: " + data.toString());
    }

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
//        mapFragment.onDestroy();
        bound = false;
        stopService(new Intent(HudActivity.this, LocationAndSensorService.class));
//        service.onDestroy();
    }


}
