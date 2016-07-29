package com.wordpress.captamericadevs.advroutes.utils;

import android.util.Log;

/**
 * Created by Parker on 7/18/2016.
 */
public class Logger {
    private static final String TAG = "AdvRoutes";

    //Methods for VERBOSE logging
    public static void v(String str) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, str);
        }
    }
    public static void v(String str, Throwable t) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, str, t);
        }
    }

    //Methods for DEBUG logging
    public static void d(String str) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, str);
        }
    }
    public static void d(String str, Throwable t) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, str, t);
        }
    }

    //Methods for INFO logging
    public static void i(String str) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, str);
        }
    }
    public static void i(String str, Throwable t) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, str, t);
        }
    }

    //Methods for WARN logging
    public static void w(String str) {
        if (Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, str);
        }
    }
    public static void w(String str, Throwable t) {
        if (Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, str, t);
        }
    }

    //Methods for ERROR logging
    public static void e(String str) {
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, str);
        }
    }
    public static void e(String str, Throwable t) {
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, str, t);
        }
    }

}

