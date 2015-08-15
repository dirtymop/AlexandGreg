package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.dirtymop.myapplication.classes.AndroidWear;
import com.example.dirtymop.myapplication.classes.ContactsTable;
import com.example.dirtymop.myapplication.classes.DatabaseHelper;
import com.example.dirtymop.myapplication.classes.HistoryTable;
import com.example.dirtymop.myapplication.fragments.RetainedFragment;
import com.example.dirtymop.myapplication.services.LocationAndSensorService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HudActivity
        extends Activity
        implements ServiceConnection,
        OnMapReadyCallback,
        GoogleMap.SnapshotReadyCallback {

    private static final String TAG_FRAG_RETAINED = "RetainedFragment";
    TextView distanceView, speedView, connectionText;//latitude, longitude, accuracy, altitude, speedView, acceleration;
    Button closeHud;
    LinearLayout connectionLayout;
    ProgressBar progressBar;

    private int LOCATION_DISTANCE_REFRESH = 0;  // meters
    private int LOCATION_TIME_REFRESH = 500;    // milliseconds

    // Fragments
    FragmentManager fm;
    MapFragment mapFragment;
    RetainedFragment retainedFragment;
    private static final String TAG_FRAG_MAP = "map_fragment";

    // Google Maps
    GoogleMap googleMap;
    LatLng lastLatLng = null;
//    HashMap<String, String> route;
//    ArrayList<String> route;
    HashMap<LatLng, String> markers = new HashMap<LatLng, String>();
    ArrayList<Double> altitude = new ArrayList<Double>();
    ArrayList<Float> speed = new ArrayList<Float>();
    Float distance;
    String startTime, endTime;
    Bitmap routeSnapshot;
    ArrayList<LatLng> currentRoute = new ArrayList<LatLng>();
    int routeIndex;
    private final float ZOOM_LEVEL = 18;
    private final int ZOOM_DURATION = 1000; // milliseconds
    private Marker currentMarker = null;
    boolean isRestored = false;
    volatile boolean snapshotIsReady = false;

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

        Log.d("hud", "beginning map init...");
        // Initialize Google Maps fragment
        initMap();
        // Assign the current activity to receive callbacks from Google Maps.
        setMapCallbacks(this);
        Log.d("hud", "map init finished!");


        IntentFilter intentFilter = new IntentFilter("toActivity");
        registerReceiver(activityReceiver, intentFilter);
        createService();

        connectionText = (TextView) findViewById(R.id.connectionText);
        connectionText.setText("waiting for GPS...");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        connectionLayout = (LinearLayout) findViewById(R.id.connectionLayout);
        connectionLayout.setVisibility(View.VISIBLE);

        speedView = (TextView) findViewById(R.id.speed);
        distanceView = (TextView) findViewById(R.id.distance);
        closeHud = (Button) findViewById(R.id.closeHudButton);
        closeHud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                connectionLayout.setVisibility(View.VISIBLE);
//                connectionText.setText("saving route...");
                endTime = getCurrentTimeMillis();
                takeSnapshot();
            }
        });

        if (getFragmentManager().findFragmentByTag(TAG_FRAG_RETAINED) == null) {
            retainedFragment = new RetainedFragment();
            retainedFragment.setRetainInstance(true);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(retainedFragment,TAG_FRAG_RETAINED);
            fragmentTransaction.commit();
        }

        if (savedInstanceState != null) {
            //Every time during the recreate of the activity, the retainedFragment will be lost, so we need to reassign the retainedFragment
            retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(TAG_FRAG_RETAINED);
            retainedFragment.setRetainInstance(true);
            currentRoute = retainedFragment.getRoute();
            aw = retainedFragment.getAndroidWear();
//            route = retainedFragment.getHashRoute();
            routeIndex = retainedFragment.getRouteIndex();

            speedView.setText(String.format("%.2f", retainedFragment.getSpeed()) + " [mph]");
            distanceView.setText(String.format("%.2f", retainedFragment.getDistance()) + " [m]");

            isRestored = true;
        }
        else {
            // Initialize Android Wear
            aw = new AndroidWear(this);
            retainedFragment.setAndroidWear(aw);

            // Create new hashmap for route
//            route = new HashMap<String, String>();
//            retainedFragment.updateHashRoute(route);

            currentRoute = new ArrayList<LatLng>();
            retainedFragment.updateRoute(currentRoute);

            routeIndex = 0;
            retainedFragment.setRouteIndex(routeIndex);
        }


        //testing history view ----------------------------------------------
      /* DatabaseHelper x= new DatabaseHelper(this);
        SQLiteDatabase mydb = x.databaseOpenOrCreate("local.db");
        x.createTables(mydb);
        //ArrayList<HistoryTable> thehistorytable=x.getHistoryEntry(mydb);
        HistoryTable newHistorytable= new HistoryTable();
        newHistorytable.setAvgspeed("fred");
        newHistorytable.setFacebookID("fred");
        newHistorytable.setTime("zoro");
        newHistorytable.setElevation("lots");
        newHistorytable.setCustomerName("1234567890");
        newHistorytable.setDate("yo");
        newHistorytable.setlatsandlong("1234567890");


        x.insertHistoryEntry(mydb,newHistorytable);
        */
        //-------------------------------------------------------------------

//        // Create service
//        IntentFilter intentFilter = new IntentFilter("toActivity");
//        registerReceiver(activityReceiver, intentFilter);
//        createSerice();

        // Initialize TextView
//        latitude = (TextView) findViewById(R.id.latitude);
//        longitude = (TextView) findViewById(R.id.longitude);
//        accuracy = (TextView) findViewById(R.id.accuracy);
//        altitude = (TextView) findViewById(R.id.altitude);
//        speedView = (TextView) findViewById(R.id.speedView);
//        distance = (TextView) findViewById(R.id.distance);
//        acceleration = (TextView) findViewById(R.id.acceleration);
//        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // Plotting
//        xAccelerationSeries = new SimpleXYSeries("x-axis");
//        yAccelerationSeries = new SimpleXYSeries("y-axis");
//        zAccelerationSeries = new SimpleXYSeries("z-axis");
//        plot.setDomainBoundaries(-10, 10, BoundaryMode.AUTO);
//        plot.setRangeBoundaries(-10, 10, BoundaryMode.AUTO);
//        plot.setTitle("Acceleration Data");
//        plot.addSeries(xAccelerationSeries, new LineAndPointFormatter(Color.BLACK, Color.RED, null, null));
//        plot.addSeries(yAccelerationSeries, new LineAndPointFormatter(Color.BLACK, Color.WHITE, null, null));
//        plot.addSeries(zAccelerationSeries, new LineAndPointFormatter(Color.BLACK, Color.BLUE, null, null));
//        final PlotStatistics altiStats = new PlotStatistics(1000, false);
//        plot.addListener(altiStats);

//        Log.d("hud", "beginning map init...");
//        // Initialize Google Maps fragment
//        initMap();
//        // Assign the current activity to receive callbacks from Google Maps.
//        setMapCallbacks(this);
//        Log.d("hud", "map init finished!");
//
//        // Initialize Android Wear
//        aw = new AndroidWear(this);
//
//        // Create new hashmap for route
//        route = new HashMap<String, String>();
//        routeIndex = 0;
    }

    private void takeSnapshot() {
        googleMap.snapshot(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        Log.d("hud", "saving instance state");
        retainedFragment.setAndroidWear(aw);
        retainedFragment.setRouteIndex(routeIndex);
        retainedFragment.updateRoute(currentRoute);
//        retainedFragment.updateHashRoute(route);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!this.isFinishing())
            if (bound)
                this.unbindService(this); bound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("hud", "[onResume] service is started: " + serviceIsStarted());
        if (serviceIsStarted()) bindWithService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        googleMap.snapshot(this);
//
//        // Send route data to the local database.
//        saveRouteToLocalDB();

        // Destroy all the things...
        cleanup();
    }

    public void updateLocation(Bundle location) {

        connectionLayout.setVisibility(View.GONE);

        // Set as current location
        LatLng current = new LatLng(location.getDouble("latitude"), location.getDouble("longitude"));

        // Store each entry.
        currentRoute.add(current);
        altitude.add(location.getDouble("altitude"));
        speed.add(location.getFloat("speed"));
        distance = location.getFloat("distance");

        // Set text in floating window.
        speedView.setText(String.format("%.2f", location.getFloat("speed")) + " [mph]");
        distanceView.setText(String.format("%.2f", location.getFloat("distance")) + " [m]");

        // Push items to retained fragment.
        retainedFragment.setDistance(location.getFloat("distance"));
        retainedFragment.setSpeed(location.getFloat("speed"));

        // Add lat/lng to Google Maps
        updatePrimaryPath(current);
        centerMapOnLocation(current);

        // Update marker to current location
        markerOnLocation(current, "You are here!");

        // Save latitude and longitude to the map
//        String locationString = Double.toString(current.latitude)
//                        + ","
//                        + Double.toString(current.longitude);
//        route.put(Integer.toString(routeIndex), locationString);
//        route.add(locationString);
        routeIndex++; // Increment the route index.

        Log.d("hud","updating location");

        aw.sendLatLng(current);
    }

    public boolean isOverThreshold(double[] mobile, double[] wear) {
        Log.d("emergency", "\nmobile:"
            + mobile[0] + "," + mobile[1] + "," + mobile[2]);
        Log.d("emergency", "\nwear:"
            + wear[0] + "," + wear[1] + "," + wear[2]);
        if ((Math.abs(mobile[0]) > 8.0 || Math.abs(mobile[1]) > 8.0 || Math.abs(mobile[2]) > 8.0) && (Math.abs(wear[0]) > 8.0 || Math.abs(wear[1]) > 8.0 || Math.abs(wear[2]) > 8.0))
        {
            Log.d("emergency", "threshold reached!");
            return true;
        }
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

//            // Set textview text.
//            acceleration.setText("Acceleration peaks:"
//                    + "\nX: " + xHigh
//                    + "\nY: " + yHigh
//                    + "\nZ: " + zHigh);
//
//            // Time for domain plot
//            Calendar c = Calendar.getInstance();
//            long now = c.getTimeInMillis();
//
//            // Only plot 50 entries at a time.
//            if (xAccelerationSeries.size() > 50){
//                xAccelerationSeries.removeFirst();
//                yAccelerationSeries.removeFirst();
//                zAccelerationSeries.removeFirst();
//            }
//
//            // Add entry to each data series.
//            xAccelerationSeries.addLast(now, data.getDouble("x-axis"));
//            yAccelerationSeries.addLast(now, data.getDouble("y-axis"));
//            zAccelerationSeries.addLast(now, data.getDouble("z-axis"));
//
//            // Redraw the plot with the new data.
//            plot.redraw();
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
        if (isOverThreshold(new double[]{xMobile, yMobile, zMobile}, new double[]{xWear, yWear, zWear})) {
            handleEmergency();
        }
    }

    // Handles all methods pertaining to emergency response.
    private void handleEmergency() {
//        Toast.makeText(this, "EMERGENCY: calling, 5404197390", Toast.LENGTH_SHORT).show();

        // Ensure user can make phone calls.
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {

            // creates new contact varible
            ContactsTable EMS=new ContactsTable();
            DatabaseHelper dbhelper= new DatabaseHelper(this.getApplicationContext());
            SQLiteDatabase mydb = dbhelper.databaseOpenOrCreate("local.db");
            dbhelper.createTables(mydb);
            EMS=dbhelper.getContact(mydb);
            //----------------------------------------------------------
            if (EMS != null) {
                Toast.makeText(this, "\"EMERGENCY: calling, " + EMS.getName(), Toast.LENGTH_SHORT).show();
                call(EMS.getNumber());
            }
            else {
                Toast.makeText(this, "No emergency contacts found.\nConsider adding one...", Toast.LENGTH_SHORT).show();
            }

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
    public void createService() {
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
    private void markerOnLocation(LatLng loc, String message) {
        if (currentMarker != null)  {
            currentMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(loc)
                .visible(true)
                .title(message)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_dot));
        currentMarker = googleMap.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("hud", "map is ready");

        startTime = getCurrentTimeMillis();

        connectionLayout.setVisibility(View.GONE);

        // Set the local Google Map object.
        this.googleMap = googleMap;

        Log.d("test", "retainedFragment.hasStartingContent(): " + retainedFragment.hasStartingContent());
        // Check if the retained fragment has content waiting.
        if (retainedFragment.hasStartingContent()) {

            // Save markers.
            markers = retainedFragment.getMarkers();

            Log.d("test", "loaded " + markers.size() + " markers onto the map.");
            Toast.makeText(this, "loaded " + markers.size() + " markers onto the map.", Toast.LENGTH_SHORT);

            // Iterate through all markers and populate on the map.
            Iterator it = markers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                this.googleMap.addMarker(
                        new MarkerOptions()
                                .position((LatLng) pair.getKey())
                                .title((String) pair.getValue())
                );
                it.remove(); // avoids a ConcurrentModificationException
            }
        }


        if (isRestored) {
            for (int i = 0; i < currentRoute.size(); i++) {
                updatePrimaryPath(currentRoute.get(i));
            }
            centerMapOnLocation(currentRoute.get(currentRoute.size()-1));
            markerOnLocation(currentRoute.get(currentRoute.size()-1), "You are here!");

            isRestored = false;
        }
    }
    /*
    * -------------------------
    * */

    // Save route to local database
    public void saveRouteToLocalDB() {
//        // Instance of database helper class
//        dbHelper = new DatabaseHelper(this);
//        // Instance of local SQLite database
//        db = dbHelper.databaseOpenOrCreate(DB_FILENAME);

        // TODO: put data into local DB here
        //
        // db.insertRoute(...)

        // Encode Route
        String encodedRoute = "";
        for (LatLng point : currentRoute) {

            // Convert Lat/Lng pair into String based on schema.
            encodedRoute = encodedRoute
                    + Double.toString(point.latitude)
                    + ","
                    + Double.toString(point.longitude);

            // If point is not the last one, add delimeter for next item.
            if (point != currentRoute.get(currentRoute.size()-1))
                encodedRoute = encodedRoute + ";";
        }

        // Encode altitude
        String encodedAltitude = "";
        for (Double point : altitude) {

            // Convert altitude into String based on schema.
            encodedAltitude = encodedAltitude
                    + Double.toString(point);

            // If point is not the last one, add delimeter for next item.
            if (point != altitude.get(altitude.size()-1))
                encodedAltitude = encodedAltitude + ",";
        }

        // Encode speed.
        String encodedSpeed = "";
        for (Float point : speed) {

            // Convert altitude into String based on schema.
            encodedSpeed = encodedSpeed
                    + Double.toString(point);

            // If point is not the last one, add delimeter for next item.
            if (point != speed.get(altitude.size()-1))
                encodedSpeed = encodedSpeed + ",";
        }

        // Encode distance.
        String encodedDistance = Float.toString(distance);

        // Encode markers.
        String encodedMarkers = "";
        Iterator it = markers.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();

            // Convert altitude into String based on schema.
            encodedMarkers = encodedMarkers
                    + Double.toString(((LatLng) pair.getKey()).latitude)
                    + ","
                    + Double.toString(((LatLng) pair.getKey()).longitude)
                    + "~"
                    + pair.getValue();

            // If point is not the last one, add delimeter for next item.
            if (it.hasNext())
                encodedMarkers = encodedMarkers + ";";

            it.remove(); // avoids a ConcurrentModificationException
        }

        // Convert snapshot bitmap to string.
        String encodedBitmap = " ";
        if (snapshotIsReady)
            encodedBitmap = convertBitmapToString(routeSnapshot);

        dbHelper.insertHistoryEntry(
                db,
                new HistoryTable(
                        "blah",
                        "dude",
                        encodedRoute,
                        getCurrentDate(),
                        endTime,
                        encodedAltitude,
                        encodedSpeed,
                        encodedDistance,
                        encodedBitmap,
                        encodedMarkers,
                        startTime,
                        " "
                )
        );

        Log.d("hud", "saving route..."
                        + "\nlocal DB: " + DB_FILENAME
                        + "\nencodedRoute: " + encodedRoute
                        + "\nencodedAltitude: " + encodedAltitude
                        + "\nencodedSpeed: " + encodedSpeed
                        + "\nendTime: " + endTime
                        + "\ngetCurrentDate(): " + getCurrentDate()
                        + "\nencodedDistance: " + encodedDistance
                        + "\nencodedBitmap: " + encodedBitmap
                        + "\nencodedMarkers: " + encodedMarkers

        );

        db.close();
    }

    // Gets the current time and date in a formatted string.
    public String getCurrentTime() {
        // Get current instance of the calendar.
        Calendar c = Calendar.getInstance();

        // Create a format for the date.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Return the formatted string.
        return sdf.format(c.getTime());
    }

    // Gets the current time and date in a formatted string.
    public String getCurrentDate() {
        // Get current instance of the calendar.
        Calendar c = Calendar.getInstance();

        // Create a format for the date.
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

        // Return the formatted string.
        return sdf.format(c.getTime());
    }

    // Gets the current time in millis.
    public String getCurrentTimeMillis() {
        // Get current instance of the calendar in millis and return.
        return ("" + Calendar.getInstance().getTimeInMillis());
    }

    public Bitmap cropBitmap(Bitmap src) {
        if (src.getWidth() >= src.getHeight()) {
            return Bitmap.createBitmap(
                    src,
                    (src.getWidth()/2 - src.getHeight()/2),
                    0,
                    src.getHeight(),
                    src.getHeight()
            );
        }
        else {
            return Bitmap.createBitmap(
                    src,
                    0,
                    (src.getHeight()/2 - src.getWidth()/2),
                    src.getWidth(),
                    src.getWidth()
            );
        }
    }

    public String convertBitmapToString(Bitmap src) {
        // Crop the image.
        src = cropBitmap(src);

        // Convert to stream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, baos);

        // Convert to byte array.
        byte[] b = baos.toByteArray();

        // Send string-encoded byte array.
        return Base64.encodeToString(b, Base64.DEFAULT);
    }



    // Method for tidying up onDestroy.
    public void cleanup() {
//        mapFragment.onDestroy();
        bound = false;
        unregisterReceiver(activityReceiver);
        stopService(new Intent(HudActivity.this, LocationAndSensorService.class));
//        service.onDestroy();
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        routeSnapshot = bitmap;
        snapshotIsReady = true;

        new EndingTask().execute();
    }

    private class EndingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            connectionLayout.setVisibility(View.VISIBLE);
            connectionText.setText("saving route...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            // Instance of database helper class
            dbHelper = new DatabaseHelper(getApplicationContext());
            // Instance of local SQLite database
            db = dbHelper.databaseOpenOrCreate(DB_FILENAME);

            saveRouteToLocalDB();

            db = dbHelper.databaseOpenOrCreate(DB_FILENAME);

            saveDBToCloud();

            db.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            connectionLayout.setVisibility(View.GONE);

            finish();
        }
    }

    private void saveDBToCloud() {
        dbHelper.Savetothecloud(dbHelper.getHistoryEntry(db), null, null);
    }
}
