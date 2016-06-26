package com.wordpress.captamericadevs.advroutes;

import android.support.v4.app.FragmentManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wordpress.captamericadevs.advroutes.fragments.ButtonPanel;
import com.wordpress.captamericadevs.advroutes.utils.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    //private ButtonPanel mBPanel;
    private ImageButton buttonRide;
    private ImageButton buttonPlan;
    private ImageButton buttonLoad;

    //Set default LatLng
    private double longitude = 0.0;
    private double latitude = 0.0;

    private GoogleApiClient googleApiClient;

    ArrayList<LatLng> markerPoints;
    PolylineOptions lineOptions = null;
    TextView tvDistanceDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);

        buttonRide = (ImageButton) findViewById(R.id.buttonRide);
        buttonPlan = (ImageButton) findViewById(R.id.buttonPlan);
        buttonLoad = (ImageButton) findViewById(R.id.buttonLoad);
        buttonRide.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //mBPanel = new ButtonPanel();
        //setFragment(mBPanel);


        markerPoints = new ArrayList<LatLng>();
    }

    public void setFragment(Fragment frag)
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.buttonpanel) == null) {
            fm.beginTransaction().add(R.id.buttonpanel, frag).commit();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == buttonRide) {
            getDirections();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
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

        // Add a marker and move the camera
        getCurrentLocation(); //Place marker at current location if available
        LatLng latLong = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    //Changed from void to LatLng
    private void getCurrentLocation(){
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }

    private void moveMap() {
        String msg = latitude + ", "+ longitude;
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Start Here"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (markerPoints.size() >= 10) //if there are multiple points, clear them all
        {
            return;
        }

        markerPoints.add(latLng); //add point to our array
        int size = markerPoints.size();
        drawMarkers(latLng, size);
    }

    public void drawMarkers(LatLng latLng, int size){
        MarkerOptions nextMarker = new MarkerOptions();
        nextMarker.position(latLng);
        nextMarker.draggable(true);

        if (size == 2) //set the end point to RED
        {
            nextMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        else if (size == 1) //set the start to GREEN
        {
            nextMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        else
        {
            nextMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }

        mMap.addMarker(nextMarker);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (markerPoints.size() > 0) //if there are multiple points, clear them all
        {
            mMap.clear(); //clear the map
            markerPoints.clear();
            tvDistanceDuration.setText("Distance: ~, Duration: ~");
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

        /** A method to download json data from url */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while( ( line = br.readLine()) != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                Log.d("Exception downloading", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

            }

            // Adding all the points in the route to LineOptions
            lineOptions = new PolylineOptions()
                    .color(Color.BLUE)
                    .width(8)
                    .addAll(points);
            mMap.addPolyline(lineOptions);

            tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
        }
    }

    public void getDirections(){
        int size = markerPoints.size();
        //sort the point list if the final destination is in position 2 followed by waypoints
        if(size > 2) {
            markerPoints.add(markerPoints.get(1)); //put destination at end
            markerPoints.remove(markerPoints.get(1)); //remove destination at position 2 and shift elements left
        }
        if(size >= 2) {
            //loop through the waypoint list a draw route
            for (int j = 0; j <= size-2; j++) {
                LatLng origin = markerPoints.get(j);
                LatLng dest = markerPoints.get(j+1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        moveMap();
    }
}
