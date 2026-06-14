package com.agrovet.pos;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioNotificaciones extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMsg";
    private static final String CHANNEL_ID = "default_channel_id";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(TAG, " Nuevo token Firebase: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.i(TAG, "Mensaje recibido de: " + message.getFrom());

        String title = "AgroVet";
        String body = "Tienes una nueva notificación";

        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) {
                title = message.getNotification().getTitle();
            }
            if (message.getNotification().getBody() != null) {
                body = message.getNotification().getBody();
            }
            Log.i(TAG, "Título: " + title + " | Cuerpo: " + body);
        }

        showNotification(title, body);
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
        if (icon == 0) {
            icon = android.R.drawable.ic_dialog_info;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));

        notificationManager.notify(100, builder.build());
        Log.i(TAG, "Notificación mostrada");
    }
}