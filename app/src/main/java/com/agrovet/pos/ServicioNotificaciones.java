package com.agrovet.pos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioNotificaciones extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getNotification() != null) {

            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            Log.i("FCM", "title: " + title + "body: " + body);
            showNotification(title, body);
        }
    }

    public void showNotification(String title,String body)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("CANAL PREDETERMINADO PARA RECIBIR NOTIFICACIONES");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.bg_circle_teal)
                .setColor(ContextCompat.getColor(this, R.color.teal_light))
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManager.notify(0, builder.build());
    }



}
