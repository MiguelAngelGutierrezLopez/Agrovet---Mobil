package com.agrovet.pos.utils;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppLogger {
    private static final String TAG = "AgrovetApp";
    private static final List<String> logHistory = new ArrayList<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void e(String message, Throwable throwable) {
        String logEntry = formatLog("ERROR", message + (throwable != null ? ": " + throwable.getMessage() : ""));
        Log.e(TAG, message, throwable);
        addToHistory(logEntry);
    }

    public static void d(String message) {
        String logEntry = formatLog("DEBUG", message);
        Log.d(TAG, message);
        addToHistory(logEntry);
    }

    public static void i(String message) {
        String logEntry = formatLog("INFO", message);
        Log.i(TAG, message);
        addToHistory(logEntry);
    }

    public static void w(String message) {
        String logEntry = formatLog("WARN", message);
        Log.w(TAG, message);
        addToHistory(logEntry);
    }

    private static String formatLog(String level, String message) {
        return String.format("[%s] %s: %s", dateFormat.format(new Date()), level, message);
    }

    private static synchronized void addToHistory(String logEntry) {
        logHistory.add(0, logEntry);
        if (logHistory.size() > 200) {
            logHistory.remove(logHistory.size() - 1);
        }
    }

    public static synchronized List<String> getLogHistory() {
        return new ArrayList<>(logHistory);
    }

    public static synchronized void clearHistory() {
        logHistory.clear();
    }
}
