package com.agrovet.pos.utils;

import android.util.Log;
import java.util.Map;
import java.util.List;

public class DebugLog {
    private static final String TAG = "AgrovetDebug";

    public static void sql(String table, String action, Object data) {
        Log.d(TAG, " [SQL] " + action + " en " + table + ": " + (data != null ? data.toString() : "null"));
    }

    public static void api(String endpoint, String status, Object response) {
        String msg = " [API] " + endpoint + " -> " + status;
        if (response != null) {
            msg += " | Datos: " + response.toString();
        }
        Log.i(TAG, msg);
    }

    public static void error(String context, Throwable t) {
        Log.e(TAG, " [ERROR] " + context, t);
    }

    public static void info(String message) {
        Log.i(TAG, " [INFO] " + message);
    }
}
