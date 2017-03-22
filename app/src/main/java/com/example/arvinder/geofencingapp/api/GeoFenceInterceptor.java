package com.example.arvinder.geofencingapp.api;

import android.content.Context;

import com.example.arvinder.geofencingapp.util.GeoFenceUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by asehinsra on 3/18/17.
 */
public class GeoFenceInterceptor implements Interceptor {
    private final Context context;

    public GeoFenceInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String responseString = GeoFenceUtil.readAsset(context, "cordinates.txt");

        Response response = new Response.Builder()
                .code(200)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                .addHeader("content-type", "application/json")
                .build();

        return response;
    }
}
