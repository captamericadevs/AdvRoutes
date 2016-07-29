package com.wordpress.captamericadevs.advroutes.controllers;

import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wordpress.captamericadevs.advroutes.MapsActivity;
import com.wordpress.captamericadevs.advroutes.R;
import com.wordpress.captamericadevs.advroutes.models.MapModel;

import java.io.IOException;

/**
 * Created by Parker on 7/18/2016.
 */
public class MapController extends Application {
    private MapsActivity mActivity;
    private MapModel mMapObj;
    private GoogleMap mMap;
    private PolylineOptions mRoute;

    //default constructor
    public MapController(){
        this.mActivity = null;
        this.mMapObj = null;
        this.mMap = null;
    }

    public MapController(MapsActivity act, MapModel mapData, GoogleMap goMap){
        if (goMap == null)
            throw new IllegalArgumentException("Google Map was not initialized");
        this.mActivity = act;
        this.mMapObj = mapData;
        this.mMap = goMap;
    }

    public void setLocationEnabled(boolean value){
        mMap.setMyLocationEnabled(value);
    }

    //initialize the map display
    public void initMapSettings(LatLng latLong){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
        mMap.setOnMarkerDragListener(mActivity);
        mMap.setOnMapClickListener(mActivity);
        mMap.setOnMapLongClickListener(mActivity);
        mMap.setOnMarkerClickListener(mActivity);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //get the Google Map
    public GoogleMap getRouteMap(){
        return mMap;
    }

    public void destroyMap(){
        mMap.clear();
    }

    /**
     * reverse geocode a latlong
     * @param latLng is the geopoint
     * @return the street address of the geopoint
     */
    public String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(mActivity );

        String address = "";
        try {
            address = geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }

    /**
     * Adds a marker to the map (Start, End, Waypoint) and updates markerPoint array. Then pass it
     * to drawMarkers array
     * @param latLng
     */
    public void addMarkers(LatLng latLng){
        mMapObj.setMarkerPoint(latLng);
        int size = mMapObj.getNumMarkers();
        drawMarkers(latLng, size);
    }

    public void addPolyline(PolylineOptions options){
        mRoute = options;
        mMap.addPolyline(mRoute);
    }

    /**
     * Deletes the data on the map
     * TODO: Find a way to remove the polyline and rest it on MapLongClick
     */
    public void clearMapDisplay(){
        mMapObj.clearData(); //clear the map
    }

    /**
     * Called from addMarkers and passed a geopoint and markerPoint array size
     * Sets marker options and adds to mMap
     * @param latLng
     * @param size
     */
    private void drawMarkers(LatLng latLng, int size){
        String markerTitle = "";
        MarkerOptions nextMarker = new MarkerOptions();
        nextMarker.position(latLng);
        nextMarker.draggable(true);

        if (size == 2) //set the end point to RED
        {
            nextMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerTitle = "Destination";
        }
        else if (size == 1) //set the start to GREEN
        {
            nextMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerTitle = "Origin";
        }
        else
        {
            nextMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerTitle = "Waypoint";
        }

        nextMarker.title(markerTitle);
        mMap.addMarker(nextMarker);
    }

    public void getCurrentLocation(GoogleApiClient googleApiClient){
        if (ContextCompat.checkSelfPermission(mActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (location != null) {
                mMapObj.setLongitude(location.getLongitude());
                mMapObj.setLatitude(location.getLatitude());
            }
        }
    }

    /** Determines the total travel distance.
     * Currently FAILS and causes app to crash at runtime. Need to find a better method.
     * @return String total_distance
     */
    public String TotalDistance(){
        int legs = mMapObj.getNumDistances();
        Double totalDist = 0.0;
        String temp;
        String metric = "";
        for(int i = 0; i < legs; i++) {
            //are we using the metric system?
            if (mMapObj.getDistance(i).contains("km")){
                metric = getResources().getString(R.string.km);
            } else {
                metric = getResources().getString(R.string.mi);
            }
            temp = mMapObj.getDistance(i).replaceAll("[a-z]", ""); //get rid of alphabet and parsedouble
            totalDist = totalDist + Double.parseDouble(temp);
        }
        mMapObj.setTotalDist(totalDist); //store total
        String msg = "Distance: "+ mMapObj.getTotalDist() + metric;
        return msg;
    }

    private void moveMap() {
//        String msg = latitude + ", "+ longitude;
//        LatLng latLng = new LatLng(latitude, longitude);
//
//        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Origin"));
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
