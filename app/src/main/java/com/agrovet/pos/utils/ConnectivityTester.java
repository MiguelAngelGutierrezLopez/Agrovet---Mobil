package com.agrovet.pos.utils;

import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectivityTester {
    private static final String TAG = "AgrovetTest";

    public interface OnTestResult {
        void onResult(String service, boolean success, String error);
    }

    public static void testService(String host, int port, String serviceName, OnTestResult callback) {
        new Thread(() -> {
            try (Socket socket = new Socket()) {
                Log.d(TAG, "Probando conexión a: " + host);
                // Para dominios públicos en Railway, usamos el puerto 443 (HTTPS) o el host directamente
                socket.connect(new InetSocketAddress(host, 443), 5000);
                callback.onResult(serviceName, true, null);
            } catch (IOException e) {
                Log.e(TAG, "Fallo conexión a " + serviceName + ": " + e.getMessage());
                callback.onResult(serviceName, false, e.getMessage());
            }
        }).start();
    }
}
