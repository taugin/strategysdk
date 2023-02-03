package com.github.strategy.strategy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.log.Log;


public class StartStrategyNotificationImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 3000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        Intent destIntent = null;
        RemoteViews remoteViews = null;
        int notificationId = -1;
        PendingIntent contentPendingIntent = null;
        PendingIntent fullscreenPendingIntent = null;
        boolean cancelAll = false;
        if (intent == null) {
            Log.iv(Log.TAG, "strategy notification start failed : intent is null");
            return false;
        }
        destIntent = intent.getParcelableExtra(BaseStrategyList.PREF_DEST_INTENT);
        if (destIntent == null) {
            Log.iv(Log.TAG, "strategy notification start failed : dest intent is null");
            return false;
        }
        remoteViews = destIntent.getParcelableExtra(EXTRA_REMOVE_VIEWS);
        notificationId = destIntent.getIntExtra(EXTRA_NOTIFICATION_ID, -1);
        contentPendingIntent = destIntent.getParcelableExtra(EXTRA_CONTENT_PENDING_INTENT);
        fullscreenPendingIntent = destIntent.getParcelableExtra(EXTRA_FULLSCREEN_PENDING_INTENT);
        cancelAll = destIntent.getBooleanExtra(EXTRA_CANCEL_ALL, false);
        if (remoteViews == null) {
            Log.iv(Log.TAG, "strategy notification start failed : remote views is null");
            return false;
        }
        if (notificationId < 0) {
            Log.iv(Log.TAG, "strategy notification start failed : notification id is not set");
            return false;
        }
        if (fullscreenPendingIntent == null) {
            Log.iv(Log.TAG, "strategy notification start failed : full screen intent is not set");
            return false;
        }
        try {
            showNotification(context, remoteViews, notificationId, fullscreenPendingIntent, contentPendingIntent, cancelAll);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String getName() {
        return "notification";
    }

    private void showNotification(Context context, RemoteViews remoteViews, int notificationId, PendingIntent fullScreenPending, PendingIntent contentPending, boolean cancelAll) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (cancelAll && notificationManager != null) {
                notificationManager.cancelAll();
            }
        } catch (Exception e) {
        }
        String channelId = context.getPackageName() + ".bridge_channel";
        createNotificationChannel(context, notificationManager, channelId);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(getSmallIcon(context))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCustomContentView(remoteViews);
        if (contentPending != null) {
            builder.setContentIntent(contentPending);
        }
        if (fullScreenPending != null) {
            builder.setFullScreenIntent(fullScreenPending, true);
        }
        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }

    public int getSmallIcon(Context context) {
        try {
            return context.getApplicationInfo().icon;
        } catch (Exception e) {
        }
        return 0;
    }

    private void createNotificationChannel(Context context, NotificationManager notificationManager, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(channelId,
                        getChannelName(), NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Remind Push Notification");
                notificationChannel.setSound(null, null);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
        }
    }

    private String getChannelName() {
        return "BridgeChannel";
    }
}
