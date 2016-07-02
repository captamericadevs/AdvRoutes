package com.wordpress.captamericadevs.advroutes.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import com.google.android.gms.maps.model.LatLng;
import com.wordpress.captamericadevs.advroutes.MapsActivity;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Parker on 6/27/2016.
 */
public class ParserTask extends AsyncTaskLoader<ArrayList<LatLng>> {

    public ArrayList<String> timeTable;
    private ArrayList<LatLng> mPoints;
    private String jsonData;

    public ParserTask (Context context, String... inData){
        super(context);
        jsonData = inData[0];
        mPoints = null;
    }

    @Override
    public ArrayList<LatLng> loadInBackground(){
        //Runs in the background thread and generates a new data set to be passed back to UI thread

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try{
            jObject = new JSONObject(jsonData);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<LatLng> data = processRouteLegs(routes);

        return data;
    }

    @Override
    public void deliverResult(ArrayList<LatLng> result) {
        if(isReset()){
            return;
        }

        if(isStarted()) {
            super.deliverResult(result);
        }

        mPoints = result;
//
//        tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
    }

    /** A method to download json data from url */
    private ArrayList<LatLng> processRouteLegs(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = new ArrayList<>();
        String distance = "";
        String duration = "";
        timeTable = new ArrayList<>();

        //if no results passed, set a default value and bail
        if(result.size() < 1){
            points.add(new LatLng(0.0,0.0));
            return points;
        }

        // Traversing through all the routes
        for(int i = 0; i < result.size(); i++){

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j = 0; j < path.size(); j++){
                HashMap<String,String> point = path.get(j);

                if(j == 0){    // Get distance from the list
                    distance = (String)point.get("distance"); //TODO: Get these back to UI Thread
                    timeTable.add(distance);
                    continue;
                }else if(j==1){ // Get duration from the list
                    duration = (String)point.get("duration");
                    timeTable.add(duration);
                    continue;
                }

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
        }

        return points;
    }

    @Override
    protected void onStartLoading() {
        if(mPoints != null){
            deliverResult(mPoints);
        }
        else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        mPoints = null;
    }

    @Override
    public void onCanceled(ArrayList<LatLng> data) {
        super.onCanceled(data);
    }

}
