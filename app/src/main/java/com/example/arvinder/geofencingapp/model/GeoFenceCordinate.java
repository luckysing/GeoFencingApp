package com.example.arvinder.geofencingapp.model;

/**
 * Created by asehinsra on 3/18/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoFenceCordinate implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("radius")
    @Expose
    private Integer radius;
    @SerializedName("name")
    @Expose
    private String name;
    public final static Parcelable.Creator<GeoFenceCordinate> CREATOR = new Creator<GeoFenceCordinate>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GeoFenceCordinate createFromParcel(Parcel in) {
            GeoFenceCordinate instance = new GeoFenceCordinate();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.lat = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.lon = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.radius = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public GeoFenceCordinate[] newArray(int size) {
            return (new GeoFenceCordinate[size]);
        }

    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(lat);
        dest.writeValue(lon);
        dest.writeValue(radius);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }

}
