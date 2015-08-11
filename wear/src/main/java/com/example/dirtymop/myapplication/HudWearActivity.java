package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.DataInput;
import java.util.ArrayList;

public class HudWearActivity
        extends Activity
        implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraChangeListener {

    // Member variables
    private DismissOverlayView mDismissOverlay;
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    private FragmentManager fm;
    private static final String TAG_FRAG_MAP = "map_fragment";
    private GoogleApiClient googleApiClient;

    /*
    * Sensor member variables
    * */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayList<Float> accelData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hud_wear);

        // Set up dismiss overlay
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.basic_wear_long_press_intro);
        mDismissOverlay.showIntroIfNecessary();

        // Set up fragment manager
        fm = getFragmentManager();

        // Set up map fragment
        mapFragment = (MapFragment) fm.findFragmentByTag(TAG_FRAG_MAP);
        if (mapFragment == null) {
            // Initialize map options
            GoogleMapOptions mapOptions = new GoogleMapOptions();
            mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                    .compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .tiltGesturesEnabled(true);
            mapFragment = MapFragment.newInstance(mapOptions);
        }
        mapFragment.getMapAsync(this);

        // Add map to DismissOverlayView
        fm.beginTransaction().add(R.id.mapWearFrame, mapFragment).commit();

        // Iitialize Google API client.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API) // Request access only to the wearable API.
                    .build();
        }
        if (!googleApiClient.isConnected()) googleApiClient.connect();

        // Set up sensors
        buildSensors();
        // Assign listeners to sensors
        startSensors();

    }


    /*
    * Google Maps API methods
    * */
    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mDismissOverlay.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d("hudwear", "map is ready!");

        this.googleMap = googleMap;

        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // implement onCameraChange event.
        //
        // cameraPosition.target.latitude
        // cameraPosition.target.longitude
    }

    private void centerMapOnLocation(LatLng loc) {

        Log.d("hudwear", "centering map on location...");
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10), 1000, null);
    }

    private void markerOnLocation(LatLng loc) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(loc)
                .visible(true)
                .title("hello world");

        googleMap.addMarker(markerOptions);
    }

    /*
    * Google API client methods
    * */
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*
    * Data API Listener methods
    *
    * Runs in a background thread.
    * */
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/latlnglist") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putDouble("latitude", dataMap.getDouble("latitude"));
                    b.putDouble("longitude", dataMap.getDouble("longitude"));
                    m.setData(b);
                    handler.handleMessage(m);

                    Log.d("hudwear", "new data, sending message to handler");
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            LatLng current = new LatLng(msg.getData().getDouble("latitude"), msg.getData().getDouble("longitude"));

            Log.d("hudwear", "handling message");
            Log.d("hudwear", "lat: " + current.latitude);
            Log.d("hudwear", "lng: " + current.longitude);

            // Handle the message
            centerMapOnLocation(current);
            markerOnLocation(current);
        }
    };

    /*
    * Sensor methods
    *
    * */
    public void buildSensors() {
        // Initialize the sensor manager.
        sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        // Initialize sensors.
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Set up array list to store data
        accelData = new ArrayList<Float>();
    }

    // Registers event listeners for each sensor.
    public void startSensors() {
        // Register listeners for each sensor
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // Gyroscope event listener.
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Determine which sensor triggered the event listener
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // Send data to phone
                Log.d("hudwear", "send accel data to mobile..."
                        + "\nx-axis: " + event.values[0]
                        + "\ny-axis: " + event.values[1]
                        + "\nz-axis: " + event.values[2]
                );
                new DataTask().execute(new Float[] {event.values[0], event.values[1], event.values[2]});
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /*
    * Send Data Async Task
    *
    * */
    public class DataTask extends AsyncTask<Float, Void, Void> {

        @Override
        protected Void doInBackground(Float... params) {

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/accellist");

            Log.d("hudwear", "params.length: " + params.length);
            putDataMapReq.getDataMap().putFloat("x-axis", params[0]);
            putDataMapReq.getDataMap().putFloat("y-axis", params[1]);
            putDataMapReq.getDataMap().putFloat("z-axis", params[2]);

            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
//            Wearable.MessageApi.sendMessage(googleApiClient)

            return null;
        }
    }


}
