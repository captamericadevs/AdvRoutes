package com.wordpress.captamericadevs.advroutes.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Parker on 7/18/2016.
 */
public class MapModel {
    private double mLongitude;
    private double mLatitude;

    private ArrayList<LatLng> markerPoints = new ArrayList<LatLng>();
    private static ArrayList<String> mDistance; //struct to hold distance of each leg
    private static ArrayList<String> mDuration; //holds duration of each leg
    private double mTotalDist;

    public MapModel(double lng, double lat){
        if (lng > 180 || lng < -180)
            throw new IllegalArgumentException(lng + "is out of bounds");
        if (lat > 85 || lat < -85)
            throw new IllegalArgumentException(lat + "is out of bounds");

        this.mLongitude = lng;
        this.mLatitude = lat;
        this.markerPoints = new ArrayList<LatLng>();
        this.mDistance = new ArrayList<String>();
        this.mDuration = new ArrayList<String>();
        this.mTotalDist = 0.0;
    }

    public void clearData(){
        markerPoints.clear();
        mDistance.clear();
        mDuration.clear();
        mTotalDist = 0.0;
    }

    //Get/Set the longitude
    public double getLongitude(){
        return mLongitude;
    }
    public void setLongitude(double lng){
        mLongitude = lng;
    }

    //Get/Set the latitude
    public double getLatitude(){
        return mLatitude;
    }
    public void setLatitude(double lat){
        mLatitude = lat;
    }

    //Get/Set the markerPoints arraylist
    public LatLng getMarkerPoint(int pPosition){
        if (pPosition > markerPoints.size())
            throw new IndexOutOfBoundsException("Index does not match a marker");
        return markerPoints.get(pPosition);
    }
    public void setMarkerPoint(LatLng point){
        markerPoints.add(point);
    }
    public void updateMarkerPoint(int index, LatLng latLong){
        if (index > markerPoints.size())
            throw new IndexOutOfBoundsException("Marker does not exist");
        markerPoints.set(index, latLong);
    }
    public void deleteMarkerPoint(int pPosition){
        if (pPosition > markerPoints.size())
            throw new IndexOutOfBoundsException("Marker is not in array");
        markerPoints.remove(pPosition);
    }
    public int getNumMarkers(){
        return markerPoints.size();
    }

    //Get/Set the distance arraylist
    public String getDistance(int legnum){
        if(legnum > mDistance.size())
            throw new IndexOutOfBoundsException("No matching leg in distance array");
        return mDistance.get(legnum);
    }
    public void setDistance(String dist){
        mDistance.add(dist);
    }
    public int getNumDistances(){
        return mDistance.size();
    }

    //Get/Set the duration arraylist
    public String getDuration(int legnum) {
        if (legnum > mDuration.size())
            throw new IndexOutOfBoundsException("No matching leg index " + legnum + " in duration array. Size() = " + mDuration.size());
        return mDuration.get(legnum);
    }
    public void setDuration(String durt){
        mDuration.add(durt);
    }
    public int getNumDuration(){
        return mDuration.size();
    }

    //Get/Set total distance
    public double getTotalDist(){
        return mTotalDist;
    }
    public void setTotalDist(double total){
        mTotalDist = total;
    }
}
