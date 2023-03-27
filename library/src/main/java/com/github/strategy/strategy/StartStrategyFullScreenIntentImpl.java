package com.github.strategy.strategy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.log.Log;
import com.github.strategy.utils.StrategyUtils;


public class StartStrategyFullScreenIntentImpl implements IStartStrategy {
    public static final int NOTIFICATION_ID = 99;

    @Override
    public int getNextStartDelay() {
        return 3000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        if (!isNeedFullscreenStrategy(intent)) {
            return false;
        }
        Log.iv(Log.TAG, "start full screen intent");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION/*65536*/);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND/*4*/);
        Notification.Builder builder = getNotificationBuilder(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 15248, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setFullScreenIntent(pendingIntent, true);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.cancel(NOTIFICATION_ID);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            StrategyUtils.postRunnableDelay(new CancelRunnable(notificationManager), 2000);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isNeedFullscreenStrategy(Intent intent) {
        boolean needFullscreenIntent = false;
        try {
            Intent destIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (destIntent != null) {
                needFullscreenIntent = destIntent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            }
        } catch (Exception e) {
        }
        return needFullscreenIntent;
    }

    @Override
    public String getName() {
        return "FullScreenIntent";
    }

    public Notification.Builder getNotificationBuilder(Context context) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("com.full.screen.id", "com.full.screen.name", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(context, notificationChannel.getId());
        } else {
            builder = new Notification.Builder(context);
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), android.R.layout.simple_spinner_item);
        builder.setContentTitle("Optimize");
        builder.setContentText("Optimizing...");
        builder.setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(remoteViews);
        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_close_clear_cancel));
        builder.setAutoCancel(true);
        builder.setDefaults(4);
        builder.setPriority(Notification.PRIORITY_LOW);
        return builder;
    }


    public static final class CancelRunnable implements Runnable {

        public final NotificationManager notificationManager;

        public CancelRunnable(NotificationManager notificationManagerCompat) {
            this.notificationManager = notificationManagerCompat;
        }

        public void run() {
            notificationManager.cancel(StartStrategyFullScreenIntentImpl.NOTIFICATION_ID);
        }
    }
}
