package com.example.arvinder.geofencingapp.controller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.arvinder.geofencingapp.api.RequestDispatcher;
import com.example.arvinder.geofencingapp.model.GeoFenceCordinate;
import com.example.arvinder.geofencingapp.service.GeofenceTransitionsIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by asehinsra on 3/19/17.
 */

public class GeoFenceController implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>, RequestDispatcher.GeoFenceReceiver, LocationListener {
    private static final String TAG = "GeoFenceController";
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 60 * 60 * 1000;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30 * 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final GoogleApiClient googleApiClient;
    private List<GeoFenceCordinate> geoFenceCordinateList;
    private final Context context;
    private Double latitude;
    private Double longitude;

    public GeoFenceController(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        RequestDispatcher requestDispatcher = new RequestDispatcher(context, this);
        requestDispatcher.requestGeoFenceCordinates();
        this.context = context;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    context,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
            GeoFenceDisplay geoFenceDisplay = (GeoFenceDisplay) context;
            geoFenceDisplay.showGeoFences(geoFenceCordinateList);
            geoFenceDisplay.animateMapToCurrentLocation(geoFenceCordinateList.get(0));
        } else {
            //log error
        }

    }

    @Override
    public void onGeofenceResults(List<GeoFenceCordinate> geoFenceCordinateList) {
        populateGeofenceList(geoFenceCordinateList);
    }

    private ArrayList<Geofence> getGeoFenceCordinateList(List<GeoFenceCordinate> geoFenceCordinateList) {
        ArrayList<Geofence> listGeoFences = new ArrayList<>();
        for (GeoFenceCordinate geoFenceCordinate : geoFenceCordinateList) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(geoFenceCordinate.getId())
                    .setCircularRegion(
                            geoFenceCordinate.getLat(),
                            geoFenceCordinate.getLon(),
                            geoFenceCordinate.getRadius()
                    )
                    .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            listGeoFences.add(geofence);
        }
        return listGeoFences;
    }

    private void populateGeofenceList(List<GeoFenceCordinate> geoFenceCordinateList) {
        this.geoFenceCordinateList = geoFenceCordinateList;
    }

    public void onStart() {
        if (!googleApiClient.isConnecting() || !googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    public void onStop() {
        if (googleApiClient.isConnecting() || googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    public void addGeofences() {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(context, "Google API Client not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(geoFenceCordinateList),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            Log.d(TAG, securityException.toString());
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        }
    }

    public void addCurrentLocationGeofence() {
        if (latitude == null || longitude == null) {
            Toast.makeText(context, "Cannot add current location Geo fence .Please wait...!", Toast.LENGTH_LONG).show();
            return;
        }
        List<GeoFenceCordinate> listGeoFenceCordinate = new ArrayList<>();
        GeoFenceCordinate geoFenceCordinate = new GeoFenceCordinate();
        geoFenceCordinate.setLat(latitude);
        geoFenceCordinate.setLon(longitude);
        geoFenceCordinate.setRadius(10); // for testing purpose set the radius to 10 it should be in the rang of 100+meters :P
        geoFenceCordinate.setId(UUID.randomUUID().toString());
        listGeoFenceCordinate.add(geoFenceCordinate);
        geoFenceCordinateList = listGeoFenceCordinate;
        addGeofences();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest(List<GeoFenceCordinate> geoFenceCordinateList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(getGeoFenceCordinateList(geoFenceCordinateList));
        return builder.build();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.getLatitude() + " : " + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    public interface GeoFenceDisplay {
        void showGeoFences(List<GeoFenceCordinate> geoFenceCordinateList);

        void animateMapToCurrentLocation(GeoFenceCordinate currentCordinate);
    }
}
