package com.github.strategy.strategy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.strategy.log.Log;
import com.github.strategy.utils.BackActUtils;


public class StartStrategyFullScreenIntentImpl implements IStartStrategy {
    public static final int NOTIFICATION_ID = 99;

    @Override
    public int getNextStartDelay() {
        return 3000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        Log.i(Log.TAG, "start");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION/*65536*/);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND/*4*/);
        NotificationCompat.Builder builder = getNotificationBuilder(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 15248, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setFullScreenIntent(pendingIntent, true);
        builder.setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        try {
            notificationManagerCompat.cancel(NOTIFICATION_ID);
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
            BackActUtils.postRunnableDelay(new CancelRunnable(notificationManagerCompat), 2000);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return "FullScreenIntent";
    }

    public NotificationCompat.Builder getNotificationBuilder(Context context) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("com.full.screen.id", "com.full.screen.name", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), android.R.layout.simple_spinner_item);
        builder.setContentTitle("Optimize");
        builder.setContentText("Optimizing...");
        builder.setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel);
        builder.setCustomContentView(remoteViews);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_close_clear_cancel));
        builder.setAutoCancel(true);
        builder.setDefaults(4);
        builder.setPriority(-1);
        return builder;
    }


    public static final class CancelRunnable implements Runnable {

        public final NotificationManagerCompat notificationManagerCompat;

        public CancelRunnable(NotificationManagerCompat notificationManagerCompat) {
            this.notificationManagerCompat = notificationManagerCompat;
        }

        public void run() {
            this.notificationManagerCompat.cancel(StartStrategyFullScreenIntentImpl.NOTIFICATION_ID);
        }
    }
}
