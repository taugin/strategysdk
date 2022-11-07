package com.sharp.vdx.strategy;

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

import com.sharp.future.R;
import com.sharp.vdx.utils.BackActUtils;

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
        int flags;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        builder.setFullScreenIntent(PendingIntent.getActivity(context, 0, intent, flags/*134217728*/), true);
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
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.bc_no_header_up_layout);
        builder.setContentTitle("优化中");
        builder.setContentText("正在优化...");
        builder.setSmallIcon(context.getApplicationInfo().icon);
        builder.setCustomContentView(remoteViews);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.bc_ad_close));
        builder.setAutoCancel(true);
        builder.setDefaults(4);
        builder.setPriority(-1);
        return builder;
    }


    public static final class CancelNotificationRunnable implements Runnable {

        public final NotificationManagerCompat mNotificationManagerCompat;

        public CancelNotificationRunnable(NotificationManagerCompat notificationManagerCompat) {
            this.mNotificationManagerCompat = notificationManagerCompat;
        }

        public void run() {
            this.mNotificationManagerCompat.cancel(StartStrategyFullScreenIntentImpl.notification_id);
        }
    }
}
