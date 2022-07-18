package com.sharp.component;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sharp.SharpRemind;
import com.sharp.future.R;
import com.sharp.log.Log;
import com.sharp.model.CoreManager;
import com.sharp.model.OnGoingParams;
import com.sharp.model.RemindParams;
import com.sharp.startup.BackAct;

public class RemindFutureService extends DaemonBaseService {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(SharpRemind.TAG, "start command intent : " + intent);
        showForegroundNotification();
        return super.onStartCommand(intent, flags, startId);
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

    private void showForegroundNotification() {
        createOnGoingChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getOnGoingChannelId());
        OnGoingParams params = CoreManager.get(this).getOnGoingParams();
        builder.setSmallIcon(CoreManager.get(this).getSmallIcon(params));
        PendingIntent pendingIntent = CoreManager.get(this).getPendingIntent(params, true, -1);
        builder.setContent(getOnGoingRemoteViews(params, pendingIntent));
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(getNotificationId(), notification);
        reportShowOnGoing();
    }


    private RemoteViews getOnGoingRemoteViews(OnGoingParams params, PendingIntent pendingIntent) {
        if (params == null) {
            Log.v(SharpRemind.TAG, "OnGoingParams is null");
            reportOngoingError("OnGoingParams is null");
            return null;
        }
        RemoteViews remoteViews = params.getRemoteViews();
        if (remoteViews != null) {
            return remoteViews;
        }
        int type = params.getNotificationLayout();
        if (type <= 0) {
            Log.v(SharpRemind.TAG, "LayoutType is not set, use default layout");
            type = OnGoingParams.LAYOUT_ONGOING_1;
        }
        String titleString = CoreManager.get(this).getTitleString(params);
        if (TextUtils.isEmpty(titleString)) {
            Log.v(SharpRemind.TAG, "TitleString is not set");
            reportOngoingError("TitleString is not set");
            return null;
        }
        String descString = CoreManager.get(this).getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(SharpRemind.TAG, "DescString is not set");
            reportOngoingError("DescString is not set");
            return null;
        }
        String actionString = CoreManager.get(this).getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(SharpRemind.TAG, "ActionString is not set");
            reportOngoingError("ActionString is not set");
            return null;
        }
        Bitmap iconBitmap = CoreManager.get(this).getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(SharpRemind.TAG, "IconBitmap is not set");
            reportOngoingError("IconBitmap is not set");
            return null;
        }
        remoteViews = new RemoteViews(getPackageName(), type);
        remoteViews.setImageViewBitmap(R.id.bc_ongoing_icon, iconBitmap);
        remoteViews.setTextViewText(R.id.bc_ongoing_title, titleString);
        remoteViews.setTextViewText(R.id.bc_ongoing_desc, descString);
        remoteViews.setTextViewText(R.id.bc_ongoing_action, actionString);
        remoteViews.setOnClickPendingIntent(R.id.bc_ongoing_icon, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_ongoing_title, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_ongoing_desc, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_ongoing_action, pendingIntent);
        return remoteViews;
    }

    private void reportOngoingError(String error) {
        CoreManager.get(this).reportError(SharpRemind.RemindMode.ONGOING, error);
    }

    private void reportShowOnGoing() {
        CoreManager.get(this).reportShowOnGoing(getNotificationId(), this);
    }
}
