package com.wordpress.captamericadevs.advroutes;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wordpress.captamericadevs.advroutes.controllers.MapController;
import com.wordpress.captamericadevs.advroutes.models.MapModel;
import com.wordpress.captamericadevs.advroutes.utils.DirectionsDownloader;
import com.wordpress.captamericadevs.advroutes.utils.DirectionsParserLoader;
import com.wordpress.captamericadevs.advroutes.utils.Logger;

import java.util.ArrayList;

/**
 * Main Activity
 *
 * @author Will Parker
 * @version 2016.0807
 */

public class MapsActivity extends AppCompatActivity implements
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener {

    private LoaderManager mSupportLoaderManager;
    public int downloadLoaderID; //way to keep track of the asynchloaders running
    public int parserLoaderID;
    private boolean mFinished = false;

    private MapModel mMapData; //Map Model object
    private MapController mMapController;

    //View objects
    private TextView mBottomSheetHeader;
    private BottomSheetBehavior mBottomSheetBehavior;

    private GoogleApiClient googleApiClient;
    private Logger mLog = new Logger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Set App View
        setAppFrames();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check location enabled or request to enable it
        checkLocationPermission();

        mSupportLoaderManager = getSupportLoaderManager();

        //Create our GoogleMap Object
        mMapData = new MapModel(0.0, 0.0);

        if(savedInstanceState != null){
            mMapData = savedInstanceState.getParcelable("MapObj");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable("MapObj",mMapData);
    }

    /**
     * Sets the main app activity view
     */
    private void setAppFrames() {
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);

        final View bottomSheet = findViewById(R.id.content_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetHeader = (TextView) findViewById(R.id.bottomSheetHeading);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
    }

    /**
     * Inflates the options menu at the top in accordance with Android Design guidelines
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_maps_action, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     */
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
    protected void onDestroy() {
        mMapController = null;

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null)
            googleApiClient.disconnect();
        super.onStop();
    }

    /**
    * Manipulates the map once available.
    * This callback is triggered when the map is ready to be used.
    * This is where we can add markers or lines, add listeners or move the camera.
    * If Google Play services is not installed on the device, the user will be prompted to install
    * it inside the SupportMapFragment. This method will only be triggered once the user has
    * installed Google Play services and returned to the app.
    * */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapController = new MapController(this, mMapData, googleMap);

        //Initialize Google Play Services
        if (ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMapController.setLocationEnabled(true);
        }

        // Move the camera
        mMapController.getCurrentLocation(googleApiClient);
        for (int i = 0; i < mMapData.getNumMarkers(); i++){
            mMapController.addMarkers(mMapData.getMarkerPoint(i), false);
        }
        if (mMapData.getPolylinePoints().size() > 1){
            mMapController.addPolyline(mMapData.getPolylinePoints());
        }
        LatLng latLong = new LatLng(mMapData.getLatitude(), mMapData.getLongitude());
        mMapController.initMapSettings(latLong);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();
    }

    /**
    * handle marker click event
    */
    @Override
    public boolean onMarkerClick(Marker marker) {
         //have the bottomsheet peek up from bottom
         if (mBottomSheetBehavior.getPeekHeight() != 300) {
             mBottomSheetBehavior.setPeekHeight(300);
             mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
         }

         //Grad the index of the selected marker (format is "m#")
         String temp = marker.getId().replaceAll("[a-zA-Z]", ""); //remove prefix
         String displayText = "";
         int index = Integer.parseInt(temp); //grab index value
         displayText = mMapController.getAddressFromLatLng((mMapData.getMarkerPoint(index)));

         if(index == 0){ //if index == 0 then you are at the origin
             mBottomSheetHeader.setText(displayText);
         } else if(index == 1){ // if index == 1 get the final leg values
             if (mMapData.getNumDuration() > 0) {
                 displayText = displayText + "\n"
                         + mMapData.getDuration(mMapData.getNumDuration() - 1) + " "
                         + mMapData.getDistance(mMapData.getNumDistances() - 1);
             }
             mBottomSheetHeader.setText(displayText);
         } else { //else, backtrack from the two markers that are out of order (orig, dest)
             if (mMapData.getNumDuration() > 1) {
                 displayText = displayText + "\n"
                         + mMapData.getDuration(index-2) + " "
                         + mMapData.getDistance(index-2);
             }
             mBottomSheetHeader.setText(displayText);
         }

         return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mMapData.getNumMarkers() >= 10) //if there are multiple points, clear them all
            return;
        mMapData.setLongitude(latLng.longitude);
        mMapData.setLatitude(latLng.latitude);
        mMapController.addMarkers(latLng, true);
    }

    /**
     * If the under long clicks the screen, clear the map and MapData structures, destroy any running
     * asynchloaders
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(MapsActivity.this, "Map Cleared!", Toast.LENGTH_LONG).show();
        if (mMapData.getNumMarkers() > 0) //if there are multiple points, clear them all
        {
            mMapController.destroyMap();
            mMapController.clearMapDisplay();

            for(int i = 1; i <= parserLoaderID; i++ ) {
                mSupportLoaderManager.destroyLoader(i);
            }
            parserLoaderID = 2;
            downloadLoaderID = 1;
        }
        mBottomSheetHeader.setText(R.string.bottom_sheet_clear);

        mFinished = false;
    }

    /**
     * Take in start and end latlngs then craft RESTful url to pass to GoogleMapsAPI
     * @param origin
     * @param dest
     * @return
     */
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

    /**
     * iterate through the list of markers and call downloader/parser asynchloaders to
     * get leg data from GoogleMapsAPI in JSON format
     */
    public void getDirections(){
        int size = mMapData.getNumMarkers();
        //sort the point list if the final destination is in position 2 followed by waypoints
        mLog.i("Size = " + size);
        if(size > 2) {
            mMapData.setMarkerPoint(mMapData.getMarkerPoint(1)); //put destination at end
            mMapData.deleteMarkerPoint(1); //remove destination at position 2 and shift elements left
        }
        mLog.i("Shifted destination to end of marker array");
        if(size >= 2) {
            //loop through the waypoint list a draw route
            for (int j = 0; j <= size-2; j++) {
                LatLng origin = mMapData.getMarkerPoint(j);
                LatLng dest = mMapData.getMarkerPoint(j+1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                mLog.i("Retrieved URL: " + url);

                Bundle urlString = new Bundle();
                urlString.putString("url", url);
                mSupportLoaderManager.initLoader(downloadLoaderID, urlString, downloadLoaderListener);
                downloadLoaderID = downloadLoaderID + 2;
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
        //Grad the index of the selected marker (format is "m#")
        String temp = marker.getId().replaceAll("[a-zA-Z]", ""); //remove prefix
        int index = Integer.parseInt(temp); //grab index value

        //store the new lat/long in the markerPoints array
        mMapData.updateMarkerPoint(index, new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
    }

    private LoaderManager.LoaderCallbacks<String> downloadLoaderListener = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new DirectionsDownloader(MapsActivity.this, args.getString("url"));
        }


        @Override
        public void onLoadFinished(Loader<String> arg0, String arg1) {
            //feed the returned json object to the ParserLoader for parsing
            Bundle jsonData = new Bundle();
            jsonData.putString("url", arg1);
            mLog.i("Initiating ParserLoader");
            mSupportLoaderManager.initLoader(parserLoaderID, jsonData, parserLoaderListener);
            parserLoaderID = parserLoaderID + 2;
        }

        @Override
        public void onLoaderReset(Loader<String> arg0) {

        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<LatLng>> parserLoaderListener = new LoaderManager.LoaderCallbacks<ArrayList<LatLng>>() {
        @Override
        public Loader<ArrayList<LatLng>> onCreateLoader(int id, Bundle args) {
            return new DirectionsParserLoader(MapsActivity.this, mMapData, args.getString("url"));
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<LatLng>> arg0, ArrayList<LatLng> arg1) {
            mMapController.addPolyline(arg1);
            mLog.i("Created Polyline");
            //getTotDistance(mFinished);
            mFinished = true;
        }

        /**
         * Calculates the total distance of the route
         * Currently crashes at Toast invokation
         */

        private void getTotDistance(boolean isFinished){
            if(!isFinished){
//                int legs = mMapData.getNumDistances();
//                Double totalDist = 0.0;
//                String temp = "";
//                String metric = "";
//                for(int i = 0; i < legs; i++) {
//                    //are we using the metric system?
//                    if (mMapData.getDistance(i).contains("km")){
//                        metric = getResources().getString(R.string.km);
//                    } else {
//                        metric = getResources().getString(R.string.mi);
//                    }
//                    temp = mMapData.getDistance(i).replaceAll("[a-z]", ""); //get rid of alphabet and parsedouble
//                    totalDist = totalDist + Double.parseDouble(temp);
//                }
//                mMapData.setTotalDist(totalDist); //store total
//                String msg = "Distance: "+ mMapData.getTotalDist() + metric;
//                Toast.makeText(MapsActivity.this, mMapController.TotalDistance(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<LatLng>> arg0) {

        }
    };

    /* Reused from Android Tutorial Point 29 April 2016
    * http://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/ */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mLog.i("had to request location permission");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMapController.setLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}
