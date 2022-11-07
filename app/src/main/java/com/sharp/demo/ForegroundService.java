package com.sharp.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ForegroundService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showForegroundNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showForegroundNotification() {
        createOnGoingChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getOnGoingChannelId());
        builder.setSmallIcon(getApplicationInfo().icon);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getPackageManager().getLaunchIntentForPackage(getPackageName()), PendingIntent.FLAG_IMMUTABLE);
        builder.setContentTitle("Running");
        builder.setContentText("Running is desc");
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(getNotificationId(), notification);
    }

    private String getOnGoingChannelId() {
        return getPackageName() + ".ongoing";
    }

    private void createOnGoingChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = NotificationManagerCompat.from(this).getNotificationChannel(getOnGoingChannelId());
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(getOnGoingChannelId(), "daemon", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setSound(null, null);
                NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
            }
        }
    }

    private int getNotificationId() {
        try {
            return Math.abs(getPackageName().hashCode()) + 1001;
        } catch (Exception e) {
        }
        return 1001;
    }

}
