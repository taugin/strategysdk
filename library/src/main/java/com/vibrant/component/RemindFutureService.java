package com.vibrant.component;

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

import com.vibrant.VibrantRemind;
import com.vibrant.future.R;
import com.vibrant.log.Log;
import com.vibrant.model.CoreManager;
import com.vibrant.model.OnGoingParams;
import com.vibrant.model.RemindParams;
import com.vibrant.startup.BackAct;

public class RemindFutureService extends DaemonBaseService {
    private static final String TAG = "remind";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showForegroundNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showForegroundNotification();
        if (intent != null && intent.getBooleanExtra(CoreManager.EXTRA_SHOW_REMIND, false)) {
            VibrantRemind.RemindMode remindMode = VibrantRemind.RemindMode.ACTIVITY;
            try {
                remindMode = (VibrantRemind.RemindMode) intent.getSerializableExtra(CoreManager.EXTRA_REMIND_MODE);
            } catch (Exception e) {
            }
            showRemind(remindMode);
        }
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
        PendingIntent pendingIntent = CoreManager.get(this).getPendingIntent(params, true);
        builder.setContent(getOnGoingRemoteViews(params, pendingIntent));
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(getNotificationId(), notification);
    }


    private RemoteViews getOnGoingRemoteViews(OnGoingParams params, PendingIntent pendingIntent) {
        if (params == null) {
            Log.v(TAG, "OnGoingParams is null");
            reportOngoingError("OnGoingParams is null");
            return null;
        }
        int type = params.getNotificationLayout();
        if (type <= 0) {
            Log.v(TAG, "LayoutType is not set, use default layout");
            type = OnGoingParams.LAYOUT_ONGOING_1;
        }
        String descString = CoreManager.get(this).getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(TAG, "DescString is not set");
            reportOngoingError("DescString is not set");
            return null;
        }
        String actionString = CoreManager.get(this).getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(TAG, "ActionString is not set");
            reportOngoingError("ActionString is not set");
            return null;
        }
        Bitmap iconBitmap = CoreManager.get(this).getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(TAG, "IconBitmap is not set");
            reportOngoingError("IconBitmap is not set");
            return null;
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), type);
        remoteViews.setImageViewBitmap(R.id.bc_app_icon, iconBitmap);
        remoteViews.setTextViewText(R.id.bc_noti_desc, descString);
        remoteViews.setTextViewText(R.id.bc_noti_cta, actionString);
        remoteViews.setOnClickPendingIntent(R.id.bc_app_icon, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_noti_desc, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_noti_cta, pendingIntent);
        return remoteViews;
    }

    private void showRemind(VibrantRemind.RemindMode remindMode) {
        if (remindMode == VibrantRemind.RemindMode.ACTIVITY) {
            showRemindActivity();
            return;
        }
        if (remindMode == VibrantRemind.RemindMode.NOTIFICATION) {
            showRemindNotification();
            return;
        }
        if (remindMode == VibrantRemind.RemindMode.ACTIVITY_AND_NOTIFICATION) {
            showRemindNotification();
            showRemindActivity();
            return;
        }
    }

    private void showRemindActivity() {
        Intent intent = new Intent(this, RemindActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CoreManager.EXTRA_NOTIFICATION_ID, CoreManager.get(this).getRemindId());
        BackAct.startActivityBackground(this, intent);
        CoreManager.get(this).reportCallRemind();
    }

    private String getOnNotificationChannelId() {
        return getPackageName() + ".notification";
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = NotificationManagerCompat.from(this).getNotificationChannel(getOnNotificationChannelId());
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(getOnNotificationChannelId(), "daemon", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setSound(null, null);
                NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel);
            }
        }
    }

    private void showRemindNotification() {
        createNotificationChannel();
        RemindParams params = CoreManager.get(this).getRemindParams();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getOnNotificationChannelId());
        builder.setSmallIcon(CoreManager.get(this).getSmallIcon(params));
        PendingIntent pendingIntent = CoreManager.get(this).getPendingIntent(params, false);
        PendingIntent cancelPendingIntent = CoreManager.get(this).getCancelPendingIntent();
        RemoteViews remoteViews = null;
        try {
            remoteViews = getRemindRemoteViews(params, pendingIntent, cancelPendingIntent);
        } catch (Exception e) {
        }
        builder.setCustomContentView(remoteViews);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setFullScreenIntent(pendingIntent, true);
        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(CoreManager.get(this).getRemindId(), notification);
        CoreManager.get(this).reportCallNotification();
    }

    private RemoteViews getRemindRemoteViews(RemindParams params, PendingIntent pendingIntent, PendingIntent cancelPendingIntent) {
        if (params == null) {
            Log.v(TAG, "RemindParams is null");
            reportNotificationError("RemindParams is null");
            return null;
        }
        int type = params.getNotificationLayout();
        if (type <= 0) {
            Log.v(TAG, "LayoutType is not set, use default layout");
            type = RemindParams.LAYOUT_REMIND_1;
        }
        String titleString = CoreManager.get(this).getTitleString(params);
        if (TextUtils.isEmpty(titleString)) {
            Log.v(TAG, "TitleString is not set");
            reportNotificationError("TitleString is not set");
            return null;
        }
        String descString = CoreManager.get(this).getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(TAG, "DescString is not set");
            reportNotificationError("DescString is not set");
            return null;
        }
        String actionString = CoreManager.get(this).getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(TAG, "ActionString is not set");
            reportNotificationError("ActionString is not set");
            return null;
        }
        Bitmap iconBitmap = CoreManager.get(this).getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(TAG, "IconBitmap is not set");
            reportNotificationError("IconBitmap is not set");
            return null;
        }
        Bitmap imageBitmap = CoreManager.get(this).getImageBitmap(params);
        if (imageBitmap == null) {
            Log.v(TAG, "ImageBitmap is not set");
            reportNotificationError("ImageBitmap is not set");
            return null;
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), type);
        remoteViews.setImageViewBitmap(R.id.bc_remind_icon, iconBitmap);
        remoteViews.setTextViewText(R.id.bc_remind_title, titleString);
        remoteViews.setTextViewText(R.id.bc_remind_detail, descString);
        remoteViews.setTextViewText(R.id.bc_remind_cta, actionString);
        remoteViews.setImageViewBitmap(R.id.bc_remind_image, imageBitmap);

        remoteViews.setOnClickPendingIntent(R.id.bc_remind_icon, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_remind_title, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_remind_detail, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_remind_cta, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_remind_image, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_remind_close, cancelPendingIntent);
        return remoteViews;
    }

    private void reportNotificationError(String error) {
        CoreManager.get(this).reportError(VibrantRemind.RemindMode.NOTIFICATION, error);
    }

    private void reportOngoingError(String error) {
        CoreManager.get(this).reportError(VibrantRemind.RemindMode.ONGOING, error);
    }
}
