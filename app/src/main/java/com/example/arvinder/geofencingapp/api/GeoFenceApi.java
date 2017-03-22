package com.example.arvinder.geofencingapp.api;

import com.example.arvinder.geofencingapp.model.GeoFenceCordinate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by asehinsra on 3/18/17.
 */

public interface GeoFenceApi {
    @GET("/geofence/cordinates")
    Call<List<GeoFenceCordinate>> getListCordinates();
}
