package com.wordpress.captamericadevs.advroutes;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.wordpress.captamericadevs.advroutes.utils.DownloadTask;
import com.wordpress.captamericadevs.advroutes.utils.ParserTask;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener
{

    public int DOWNLOAD_LOADER_ID = 1;
    public int PARSER_LOADER_ID = 2;
    private DownloadTask mDownloadLoader;
    private ParserTask mParserLoader;
    private LoaderManager mSupportLoaderManager;

    private GoogleMap mMap;
    private double longitude = 0.0; //Set default LatLng
    private double latitude = 0.0;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        markerPoints = new ArrayList<LatLng>();
        mSupportLoaderManager = getSupportLoaderManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_maps_action, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_directions:
                getDirections();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        //if (v == buttonRide) {
            //getDirections();
        //}
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
            for(int i = 1; i <= PARSER_LOADER_ID; i++ ) {
                mSupportLoaderManager.destroyLoader(i);
            }
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

                Bundle urlString = new Bundle();
                urlString.putString("url", url);
                mSupportLoaderManager.initLoader(DOWNLOAD_LOADER_ID, urlString, downloadLoaderListener);
                DOWNLOAD_LOADER_ID = DOWNLOAD_LOADER_ID + 2;
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

    private LoaderManager.LoaderCallbacks<String> downloadLoaderListener = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            mDownloadLoader = new DownloadTask(MapsActivity.this, args.getString("url"));
            return mDownloadLoader;
        }


        @Override
        public void onLoadFinished(Loader<String> arg0, String arg1) {
            Bundle jsonData = new Bundle();
            jsonData.putString("url", arg1);
            mSupportLoaderManager.initLoader(PARSER_LOADER_ID, jsonData, parserLoaderListener);
            PARSER_LOADER_ID = PARSER_LOADER_ID + 2;
        }

        @Override
        public void onLoaderReset(Loader<String> arg0) {

        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<LatLng>> parserLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<LatLng>>() {
        @Override
        public Loader<ArrayList<LatLng>> onCreateLoader(int id, Bundle args) {
            mParserLoader = new ParserTask(MapsActivity.this, args.getString("url"));
            return mParserLoader;
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<LatLng>> arg0, ArrayList<LatLng> arg1) {
            PolylineOptions lineOptions = new PolylineOptions()
                .color(Color.BLUE)
                .width(8)
                .addAll(arg1);
            mMap.addPolyline(lineOptions);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<LatLng>> arg0) {

        }
    };
}
