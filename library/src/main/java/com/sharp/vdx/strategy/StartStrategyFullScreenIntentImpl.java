package com.sharp.vdx.strategy;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
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
            startWithAlarm(context, intent, 200);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return "fullscreenintent";
    }

    public NotificationCompat.Builder getNotificationBuilder(Context context) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(getChannelId(context), getChannelName(context), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context, getChannelId(context));
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
        builder.setContentTitle("优化中");
        builder.setContentText("正在优化...");
        builder.setSmallIcon(android.R.drawable.star_off);
        builder.setCustomContentView(remoteViews);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.star_off));
        builder.setAutoCancel(true);
        builder.setDefaults(4);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder;
    }

    private String getChannelId(Context context) {
        try {
            return context.getPackageName() + ".channel_id";
        } catch (Exception e) {
        }
        return "channel_id";
    }

    private String getChannelName(Context context) {
        try {
            return context.getPackageName() + ".channel_name";
        } catch (Exception e) {
        }
        return "channel_name";
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

    private static void startWithAlarm(Context context, Intent intent, int time) {
        PendingIntent activity = PendingIntent.getActivity(context, 10102, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((long) time), activity);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
