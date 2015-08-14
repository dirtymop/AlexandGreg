package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
        extends WearableActivity
        implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener {

    // Member variables
    private DismissOverlayView mDismissOverlay;
    private GoogleMap googleMap = null;
    private MapFragment mapFragment;
    private ProgressBar waitProgress;
    private TextView waitMessage;
    private LinearLayout waitLayout;
    private FrameLayout mapWearFrame;
    private FragmentManager fm;
    private static final String TAG_FRAG_MAP = "map_fragment";
    private GoogleApiClient googleApiClient;
    private int count = 0;
    private LatLng lastLatLng = null;
    private static final float ZOOM_LEVEL = 17;
    private static final int ZOOM_DURATION = 1000; // milliseconds
    private static final float PATH_LINE_WIDTH = 5;
    private volatile boolean connected = false;
    private Marker currentMarker = null;

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

        // Ambient mode
        setAmbientEnabled();

        // Set up dismiss overlay
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.basic_wear_long_press_intro);
        mDismissOverlay.showIntroIfNecessary();

        // Set up initial connection dialog, set VISIBILE
        waitLayout = (LinearLayout) findViewById(R.id.waitLayout);
        waitProgress = (ProgressBar) findViewById(R.id.waitProgress);
        waitMessage = (TextView) findViewById(R.id.waitMessage);
        waitLayout.setVisibility(View.VISIBLE);

        // Set up map wear frame, set INVISIBLE
        mapWearFrame = (FrameLayout) findViewById(R.id.mapWearFrame);
        mapWearFrame.setVisibility(View.GONE);

//        inflateMap();
        buildGoogleApiClient();
        connectGoogleApiClient();

        // Set up sensors
        buildSensors();
        // Assign listeners to sensors
        startSensors();

    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    /*
    * Google API Client
    *
    * */
    private void buildGoogleApiClient() {
        // Iitialize Google API client.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API) // Request access only to the wearable API.
                    .build();
        }
    }
    private void connectGoogleApiClient() {
        if (!googleApiClient.isConnected()) googleApiClient.connect();
    }

    /*
    * Google Maps API methods
    *
    * */
    private void inflateMap() {

        // Alter the visibiliity
        waitLayout.setVisibility(View.GONE);
        mapWearFrame.setVisibility(View.VISIBLE);

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
    }

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

        centerMapOnLocation(lastLatLng);
        markerOnLocation(lastLatLng);
    }

    private void centerMapOnLocation(LatLng loc) {
        if (googleMap != null) {
            Log.d("hudwear", "centering map on location...");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, ZOOM_LEVEL), ZOOM_DURATION, null);
        }
    }

    private void markerOnLocation(LatLng loc) {
        if (googleMap != null) {
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
    }

    private void updatePrimaryPath(LatLng current) {
        if (googleMap != null) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED).width(PATH_LINE_WIDTH).visible(true);

            if (lastLatLng != null) {
                options.add(lastLatLng);
                options.add(current);
                googleMap.addPolyline(options);
            }

            lastLatLng = current;
        }
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

                    Log.d("hudwear", "new data, sending message to handler");

                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putDouble("latitude", dataMap.getDouble("latitude"));
                    b.putDouble("longitude", dataMap.getDouble("longitude"));
                    m.setData(b);
                    handler.sendMessage(m);
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            LatLng current = new LatLng(msg.getData().getDouble("latitude"), msg.getData().getDouble("longitude"));

            Log.d("hudwear", "handling message:"
                    + "\nlat: " + current.latitude
                    + "\nlng: " + current.longitude);

            if (!connected) {
                connected = true;
                inflateMap();
                lastLatLng = current;
            }

            // Handle the message
            updatePrimaryPath(current);
            centerMapOnLocation(current);
            markerOnLocation(lastLatLng);
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
                if (count == 5) {
                    count = 0;
//                    Log.d("hudwear", "send accel data to mobile..."
//                                    + "\nx-axis: " + event.values[0]
//                                    + "\ny-axis: " + event.values[1]
//                                    + "\nz-axis: " + event.values[2]
//                    );
//                    Log.d("hudwear", "starting new task.");

                    if (connected)
                        Log.d("atest", "values: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
//                        new DataTask().execute(new Float[]{event.values[0], event.values[1], event.values[2]});
                }
                else count++;
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

            return null;
        }
    }


}
