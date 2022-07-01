package com.vibrant.startup.strategy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.vibrant.startup.utils.BackActUtils;

public class StartStrategyFullScreenIntentImpl implements IStartStrategy {
    public static int notification_id = 99;

    @Override
    public int getNextStartDelay() {
        return 3000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION/*65536*/);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND/*4*/);
        NotificationCompat.Builder builder = getNotificationBuilder(context);
        builder.setFullScreenIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT/*134217728*/), true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        try {
            notificationManagerCompat.cancel(notification_id);
            notificationManagerCompat.notify(notification_id, builder.build());
            BackActUtils.postRunnableDelay(new CancelNotificationRunnable(notificationManagerCompat), 2000);
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
//        int i = C1082R.layout.kl_layout_notification;
//        int i2 = C1082R.C1083drawable.kl_trans;
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), i);
        builder.setContentTitle("优化中");
        builder.setContentText("正在优化...");
        builder.setSmallIcon(context.getApplicationInfo().icon);
//        builder.setCustomContentView(remoteViews);
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), i2));
        builder.setAutoCancel(true);
        builder.setDefaults(4);
        builder.setPriority(-1);
        return builder;
    }


    public static final class CancelNotificationRunnable implements Runnable {

        /* renamed from: a */
        public final NotificationManagerCompat mNotificationManagerCompat;

        public CancelNotificationRunnable(NotificationManagerCompat notificationManagerCompat) {
            this.mNotificationManagerCompat = notificationManagerCompat;
        }

        public void run() {
            this.mNotificationManagerCompat.cancel(StartStrategyFullScreenIntentImpl.notification_id);
        }
    }
}
