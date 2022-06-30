package com.bigcow.component;

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

import com.bigcow.KeepAlive;
import com.bigcow.backact.BackAct;
import com.bigcow.future.R;
import com.bigcow.log.Log;
import com.bigcow.model.CoreManager;
import com.bigcow.model.OnGoingParams;
import com.bigcow.model.RemindParams;

public class BigcowFutureService extends DaemonBaseService {
    private static final String TAG = "bigcow";

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
            KeepAlive.RemindMode remindMode = KeepAlive.RemindMode.ACTIVITY;
            try {
                remindMode = (KeepAlive.RemindMode) intent.getSerializableExtra(CoreManager.EXTRA_REMIND_MODE);
            } catch (Exception e) {
            }
            showRemind(remindMode);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = NotificationManagerCompat.from(this).getNotificationChannel(getPackageName());
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(getPackageName(), "daemon", NotificationManager.IMPORTANCE_LOW);
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
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName());
        OnGoingParams params = CoreManager.get(this).getOnGoingParams();
        builder.setSmallIcon(CoreManager.get(this).getSmallIcon(params));
        PendingIntent pendingIntent = CoreManager.get(this).getPendingIntent(params);
        builder.setContent(getOnGoingRemoteViews(params, pendingIntent));
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(getNotificationId(), notification);
    }


    private RemoteViews getOnGoingRemoteViews(OnGoingParams params, PendingIntent pendingIntent) {
        if (params == null) {
            Log.v(TAG, "OnGoingParams is null");
            return null;
        }
        int type = params.getLayoutType();
        if (type <= 0) {
            Log.v(TAG, "LayoutType is not set, use default layout");
            type = OnGoingParams.LAYOUT_TYPE_1;
        }
        String descString = CoreManager.get(this).getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(TAG, "DescString is not set");
            return null;
        }
        String actionString = CoreManager.get(this).getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(TAG, "ActionString is not set");
            return null;
        }
        Bitmap iconBitmap = CoreManager.get(this).getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(TAG, "IconBitmap is not set");
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

    private void showRemind(KeepAlive.RemindMode remindMode) {
        if (remindMode == KeepAlive.RemindMode.ACTIVITY) {
            showRemindActivity();
            return;
        }
        if (remindMode == KeepAlive.RemindMode.NOTIFICATION) {
            showRemindNotification();
            return;
        }
        if (remindMode == KeepAlive.RemindMode.ACTIVITY_AND_NOTIFICATION) {
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
    }

    private void showRemindNotification() {
        RemindParams params = CoreManager.get(this).getRemindParams();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getPackageName());
        builder.setSmallIcon(CoreManager.get(this).getSmallIcon(params));
        PendingIntent pendingIntent = CoreManager.get(this).getPendingIntent(params);
        RemoteViews remoteViews = null;
        try {
            remoteViews = getRemindRemoteViews(params, pendingIntent);
        } catch (Exception e) {
        }
        builder.setContent(remoteViews);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(CoreManager.get(this).getRemindId(), notification);
    }

    private RemoteViews getRemindRemoteViews(RemindParams params, PendingIntent pendingIntent) {
        if (params == null) {
            Log.v(TAG, "OnGoingParams is null");
            return null;
        }
        int type = params.getLayoutType();
        if (type <= 0) {
            Log.v(TAG, "LayoutType is not set, use default layout");
            type = RemindParams.LAYOUT_TYPE_1;
        }
        String titleString = CoreManager.get(this).getTitleString(params);
        if (TextUtils.isEmpty(titleString)) {
            Log.v(TAG, "TitleString is not set");
            return null;
        }
        String descString = CoreManager.get(this).getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(TAG, "DescString is not set");
            return null;
        }
        String actionString = CoreManager.get(this).getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(TAG, "ActionString is not set");
            return null;
        }
        Bitmap iconBitmap = CoreManager.get(this).getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(TAG, "IconBitmap is not set");
            return null;
        }
        Bitmap imageBitmap = CoreManager.get(this).getImageBitmap(params);
        if (imageBitmap == null) {
            Log.v(TAG, "ImageBitmap is not set");
            return null;
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), type);
        remoteViews.setImageViewBitmap(R.id.bc_native_icon, iconBitmap);
        remoteViews.setTextViewText(R.id.bc_native_title, titleString);
        remoteViews.setTextViewText(R.id.bc_native_detail, descString);
        remoteViews.setTextViewText(R.id.bc_action_btn, actionString);
        remoteViews.setImageViewBitmap(R.id.bc_native_image, imageBitmap);

        remoteViews.setOnClickPendingIntent(R.id.bc_native_icon, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_native_title, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_native_detail, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_action_btn, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.bc_native_image, pendingIntent);
        return remoteViews;
    }
}
