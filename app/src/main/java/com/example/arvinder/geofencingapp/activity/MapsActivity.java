package com.example.arvinder.geofencingapp.activity;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.example.arvinder.geofencingapp.R;
import com.example.arvinder.geofencingapp.controller.GeoFenceController;
import com.example.arvinder.geofencingapp.model.GeoFenceCordinate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GeoFenceController.GeoFenceDisplay {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeoFenceController geoFenceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geoFenceController = new GeoFenceController(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void addGeofences(View view) {
        geoFenceController.addGeofences();
    }

    public void addCurrentLocationGeofence(View view) {
        geoFenceController.addCurrentLocationGeofence();
    }

    @Override
    protected void onStart() {
        super.onStart();
        geoFenceController.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        geoFenceController.onStop();
    }

    @Override
    public void showGeoFences(List<GeoFenceCordinate> geoFenceCordinateList) {
        for (GeoFenceCordinate geoFenceCordinate : geoFenceCordinateList) {
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(geoFenceCordinate.getLat(), geoFenceCordinate.getLon()))
                    .radius(geoFenceCordinate.getRadius())
                    .fillColor(Color.BLUE));
        }
    }

    @Override
    public void animateMapToCurrentLocation(GeoFenceCordinate currentCordinate) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentCordinate.getLat(), currentCordinate.getLon())));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 3000, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        geoFenceController.stopLocationUpdates();
    }

}
