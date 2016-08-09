package com.wordpress.captamericadevs.advroutes.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Object class for holding the data associated with the map (Model)
 *
 * @author Will Parker
 * @version 2016.0807
 */
public class MapModel  implements Parcelable {
    private double mLongitude;
    private double mLatitude;

    private ArrayList<LatLng> markerPoints;
    private ArrayList<LatLng> mRoute;
    private static ArrayList<String> mDistance; //struct to hold distance of each leg
    private static ArrayList<String> mDuration; //holds duration of each leg
    private double mTotalDist;
    private boolean mFill;

    public MapModel(double lng, double lat){
        if (lng > 180 || lng < -180)
            throw new IllegalArgumentException(lng + "is out of bounds");
        if (lat > 90 || lat < -90)
            throw new IllegalArgumentException(lat + "is out of bounds");

        this.mLongitude = lng;
        this.mLatitude = lat;
        this.markerPoints = new ArrayList<LatLng>();
        this.mRoute = new ArrayList<LatLng>();
        this.mDistance = new ArrayList<String>();
        this.mDuration = new ArrayList<String>();
        this.mTotalDist = 0.0;
        this.mFill = false;
    }

    public void clearData(){
        markerPoints.clear();
        mDistance.clear();
        mDuration.clear();
        mTotalDist = 0.0;
        mFill = false;
        mRoute.clear();
    }

    /******************************************************************************
     * GETTER & SETTER METHODS
     *****************************************************************************/
    public boolean getFillStatus(){
        return mFill;
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
        mFill = true;
        markerPoints.add(point);
    }
    public void setPolyline (Polyline route){
        mFill = true;
        mRoute = getPolyline(route);
    }
    public ArrayList<LatLng> getPolylinePoints(){
        return mRoute;
    }
    public ArrayList<LatLng> getPolyline(Polyline route){
        ArrayList<LatLng> tmp = new ArrayList<LatLng>();
        if (route != null) {
            Iterator<LatLng> itrTmp = route.getPoints().iterator();
            while (itrTmp.hasNext()) {
                LatLng latLong = itrTmp.next();
                tmp.addAll(Arrays.asList(latLong));
            }
            Log.i("MapsActivity", "Polyline = " + tmp.toString());
        }
        else
            tmp = null;
        return tmp;
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
            throw new IndexOutOfBoundsException("No matching leg index " + legnum
                    + " in duration array. Size() = " + mDuration.size());
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

    /******************************************************************************
     * Parcelling methods
     *****************************************************************************/
    public MapModel(Parcel in){
        mLongitude = in.readDouble();
        mLatitude = in.readDouble();
        in.readTypedList(markerPoints, LatLng.CREATOR);
        in.readTypedList(mRoute, LatLng.CREATOR);
        mDistance = (ArrayList<String>) in.readSerializable();
        mDuration = (ArrayList<String>) in.readSerializable();
        mTotalDist = in.readDouble();

        if(markerPoints.size() > 0)
            mFill = true;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mLatitude);
        dest.writeTypedList(this.markerPoints);
        dest.writeTypedList(this.mRoute);
        dest.writeSerializable(this.mDistance);
        dest.writeSerializable(this.mDuration);
        dest.writeDouble(this.mTotalDist);
    }
    public static final Parcelable.Creator<MapModel> CREATOR = new Parcelable.Creator<MapModel>(){
        public MapModel createFromParcel(Parcel in){
            return new MapModel(in);
        }
        public MapModel[] newArray(int size){return new MapModel[size];}
    };
}
