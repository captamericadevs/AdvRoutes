package com.wordpress.captamericadevs.advroutes.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Parker on 7/3/2016.
 */
public class DirectionsDownloader extends AsyncTaskLoader<String> {

    private String mData;
    private String url;

    public DirectionsDownloader (Context context, String... data){
        super(context);
        mData = null;
        url = data[0];
    }

    @Override
    public String loadInBackground(){

        // For storing data from web service
        String data = "";

        try{
            // Fetching the data from web service
            data = downloadUrl(url);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }

        return data;
    }

    @Override
    public void deliverResult(String result) {
        if(isReset()){
            return;
        }

        if(isStarted()) {
            super.deliverResult(result);
        }

        mData = result;
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

    @Override
    protected void onStartLoading() {
        if(mData != null) {
            deliverResult(mData);
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
        mData = null;
    }

    @Override
    public void onCanceled(String data) {
        super.onCanceled(data);
    }

}
