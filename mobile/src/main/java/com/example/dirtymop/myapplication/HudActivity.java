package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeriesFormatter;
import com.example.dirtymop.myapplication.services.LocationAndSensorService;

public class HudActivity
        extends Activity
        implements ServiceConnection {

    TextView latitude, longitude, accuracy, altitude, speed;
    private int LOCATION_DISTANCE_REFRESH = 0;  // meters
    private int LOCATION_TIME_REFRESH = 500;    // milliseconds

    // Location
    String locationprovider;
    LocationManager locationManager;
    LocationListener locationListener;

    // Sensor
    SensorManager sensorManager;
    Sensor gyroSensor;

    // Service member variables
    LocationAndSensorService service;
    SharedPreferences serviceStatus;
    boolean bound = false;

    // Plotting
    private XYPlot plot;
    private SimpleXYSeries altitudeSeries;
    private int HISTORY_SIZE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make activitiy fullscreen.
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set view.
        setContentView(R.layout.activity_hud);

        // Initialize TextView
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        accuracy = (TextView) findViewById(R.id.accuracy);
        altitude = (TextView) findViewById(R.id.altitude);
        speed = (TextView) findViewById(R.id.speed);
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // Plotting
        altitudeSeries = new SimpleXYSeries("altitude");
//        LineAndPointFormatter altSeriesFormat = new LineAndPointFormatter();
//        altSeriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        plot.setDomainBoundaries(0, 10, BoundaryMode.AUTO);
        plot.setRangeBoundaries(0, 10, BoundaryMode.AUTO);
        plot.addSeries(altitudeSeries, new LineAndPointFormatter(Color.RED, Color.BLACK, Color.WHITE, null));
        final PlotStatistics altiStats = new PlotStatistics(1000, false);
        plot.addListener(altiStats);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bound) this.unbindService(this); bound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindWithService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        service.onDestroy();
    }

    public void updateLocation(Bundle location) {
        latitude.setText("lat: " + Double.toString(location.getDouble("latitude")));
        longitude.setText("lon: " + Double.toString(location.getDouble("longitude")));
        altitude.setText("alt: " + Double.toString(location.getDouble("altitude")));
        accuracy.setText("acc: " + Float.toString(location.getFloat("accuracy")));
        speed.setText("speed: " + Float.toString(location.getFloat("speed")));

//        // Plot altitude
//        if (altitudeSeries.size() > HISTORY_SIZE) altitudeSeries.removeFirst();
        altitudeSeries.addLast(10, location.getDouble("accuracy"));
//        plot.redraw();
    }

    /*
    * Service connection methods
    * */
    public void bindWithService() {
        if (!serviceIsStarted()) this.startService(new Intent(HudActivity.this, LocationAndSensorService.class));
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

}
