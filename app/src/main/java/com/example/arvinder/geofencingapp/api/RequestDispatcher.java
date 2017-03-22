package com.example.arvinder.geofencingapp.api;

import android.content.Context;
import android.util.Log;

import com.example.arvinder.geofencingapp.R;
import com.example.arvinder.geofencingapp.controller.GeoFenceController;
import com.example.arvinder.geofencingapp.model.GeoFenceCordinate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by asehinsra on 3/18/17.
 */

public class RequestDispatcher {
    private final Context context;
    private GeoFenceReceiver receiver;

    public RequestDispatcher(Context context, GeoFenceController geoFenceController) {
        this.context = context;
        this.receiver = geoFenceController;
    }

    public void requestGeoFenceCordinates() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new GeoFenceInterceptor(context)).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        GeoFenceApi geoFenceApi = retrofit.create(GeoFenceApi.class);
        Call<List<GeoFenceCordinate>> call = geoFenceApi.getListCordinates();
        call.enqueue(new Callback<List<GeoFenceCordinate>>() {
            @Override
            public void onResponse(Call<List<GeoFenceCordinate>> call, Response<List<GeoFenceCordinate>> response) {
                receiver.onGeofenceResults(response.body());
            }

            @Override
            public void onFailure(Call<List<GeoFenceCordinate>> call, Throwable throwable) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                Log.d("Network error", sw.toString());
            }
        });
    }

    public interface GeoFenceReceiver {
        void onGeofenceResults(List<GeoFenceCordinate> geoFenceCordinateList);
    }
}
