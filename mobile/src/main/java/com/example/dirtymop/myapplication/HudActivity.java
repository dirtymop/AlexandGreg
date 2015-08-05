package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class HudActivity extends Activity {

    TextView latitude, longitude, accuracy, altitude;
    private int LOCATION_DISTANCE_REFRESH = 0;  // meters
    private int LOCATION_TIME_REFRESH = 500;    // milliseconds

    // Location
    String locationprovider;
    LocationManager locationManager;
    LocationListener locationListener;

    // Sensor
    SensorManager sensorManager;
    Sensor gyroSensor;

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

        // -----------------
        // Location Services
        //
        // GPS Location Provider
        startLocationTracking();


        // Motion Services
        //
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // -----------------

    }


    // GPS location
    public void startLocationTracking() {
        locationprovider = LocationManager.GPS_PROVIDER;

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
                longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
                accuracy.setText("Accuracy: " + String.valueOf(location.getAccuracy()));
                altitude.setText("Altitude: " + String.valueOf(location.getAltitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "provider enabled: " + provider, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "provider disabled: " + provider, Toast.LENGTH_LONG).show();
            }
        };

        // Schedule to receive location updates on the set listener with the given refresh intervals.
        locationManager.requestLocationUpdates(locationprovider, LOCATION_TIME_REFRESH,
                LOCATION_DISTANCE_REFRESH, locationListener);
    }
}
