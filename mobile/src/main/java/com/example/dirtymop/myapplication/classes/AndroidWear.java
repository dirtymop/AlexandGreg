package com.example.dirtymop.myapplication.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

/**
 * Created by lndsharkfury on 8/9/15.
 */
public class AndroidWear
        implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /*
    * Member Variables
    * */
    // Context of instantiating application
    private Context context;
    // Google API Client
    private GoogleApiClient googleApiClient;

    // Sending data AsyncTask
    DataTask task;

    public AndroidWear(final Context context) {
        this.context = context;


        // Iitialize Google API client.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this.context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API) // Request access only to the wearable API.
                    .build();
        }
        if (!googleApiClient.isConnected()) googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(googleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*
    * Send Data Method
    *
    * */
    public void sendLatLng(LatLng data) {

        task = new DataTask();
        task.execute(data);
    }

    /*
    * Send Data Async Task
    *
    * */
    public class DataTask extends AsyncTask<LatLng, Void, Void> {

        @Override
        protected Void doInBackground(LatLng... params) {

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/latlnglist");

            Log.d("aw", "params.length: " + params.length);
            for (int i = 0; i < params.length; i++) {
                putDataMapReq.getDataMap().putDouble("latitude", params[i].latitude);
                putDataMapReq.getDataMap().putDouble("longitude", params[i].longitude);
            }

            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(googleApiClient, putDataReq);

            return null;
        }
    }
}
