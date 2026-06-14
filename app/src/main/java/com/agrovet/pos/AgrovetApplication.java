package com.agrovet.pos;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.agrovet.pos.database.AppDatabase;

public class AgrovetApplication extends Application {

    private static final String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "Notificaciones AgroVet";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();  // ← CREA EL CANAL AL INICIAR LA APP
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getDatabase(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificaciones de AgroVet");
            channel.enableVibration(true);
            channel.setShowBadge(true);

            manager.createNotificationChannel(channel);
        }
    }
}