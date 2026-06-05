package com.agrovet.pos.utils;

import android.util.Log;

public class AppLogger {
    private static final String TAG = "AgrovetApp";

    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }
}
