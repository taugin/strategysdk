package com.sharp.model;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sharp.SharpRemind;
import com.sharp.component.RemindActivity;
import com.sharp.future.R;
import com.sharp.log.Log;
import com.sharp.startup.BackAct;
import com.sharp.startup.BackMiddleActivity;
import com.sharp.startup.adapter.StartStrategyList;

import java.util.concurrent.atomic.AtomicInteger;

public class CoreManager implements Application.ActivityLifecycleCallbacks {
    public static final String EXTRA_SHOW_REMIND = "extra_show_remind";
    public static final String EXTRA_REMIND_MODE = "extra_show_notification";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    public static final String EXTRA_ONGOING = "extra_ongoing";
    public static final String EXTRA_FROM_NOTIFICATION = "extra_from_notification";
    public static final String EXTRA_ONLY_CANCEL = "extra_only_cancel";
    private static CoreManager sCoreManager;

    public static CoreManager get(Context context) {
        if (sCoreManager == null) {
            create(context);
        }
        if (sCoreManager != null && context != null) {
            sCoreManager.mContext = context.getApplicationContext();
        }
        return sCoreManager;
    }

    private static void create(Context context) {
        synchronized (CoreManager.class) {
            if (sCoreManager == null) {
                sCoreManager = new CoreManager(context);
            }
        }
    }

    private Context mContext;
    private AtomicInteger mAtomicInteger = new AtomicInteger(0);

    private SharpRemind.OnDataCallback mOnDataCallback;
    private OnGoingParams mOnGoingParams;
    private RemindParams mRemindParams;

    private CoreManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void init() {
        registerActivityCallback();
    }

    public void setOnDataCallback(SharpRemind.OnDataCallback l) {
        mOnDataCallback = l;
    }

    public SharpRemind.OnDataCallback getOnDataListener() {
        return mOnDataCallback;
    }

    public void setOnGoingParams(OnGoingParams params) {
        mOnGoingParams = params;
    }

    public void setRemindParams(RemindParams params) {
        mRemindParams = params;
    }

    public OnGoingParams getOnGoingParams() {
        return mOnGoingParams;
    }

    public RemindParams getRemindParams() {
        return mRemindParams;
    }

    public Intent getDestIntent(Params params) {
        Intent intent = null;
        if (params != null && params.getStartClass() != null) {
            intent = new Intent(mContext, params.getStartClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            try {
                intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            } catch (Exception e) {
                intent = new Intent();
            }
        }
        Bundle bundle = getParamsBundle(params);
        if (bundle != null && intent != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    public int getNotificationId() {
        int notificationId = -1;
        if (mRemindParams != null) {
            notificationId = mRemindParams.getNotificationId();
        }
        if (notificationId == -1) {
            notificationId = 0x1234;
        }
        Log.v(SharpRemind.TAG, "notification id : " + notificationId);
        return notificationId;
    }

    public PendingIntent getPendingIntent(Params params, boolean ongoing, int notificationId) {
        PendingIntent pendingIntent = null;
        Intent intent = getDestIntent(params);
        Intent wrapIntent = new Intent(mContext, BackMiddleActivity.class);
        wrapIntent.putExtra(StartStrategyList.PREF_DEST_INTENT, intent);
        if (!ongoing) {
            wrapIntent.putExtra(CoreManager.EXTRA_NOTIFICATION_ID, notificationId);
        }
        wrapIntent.putExtra(CoreManager.EXTRA_ONGOING, ongoing);
        wrapIntent.putExtra(CoreManager.EXTRA_FROM_NOTIFICATION, true);
        int requestId = Long.valueOf(System.currentTimeMillis()).intValue() + 1001;
        try {
            int flags;
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            } else {
                flags = PendingIntent.FLAG_UPDATE_CURRENT;
            }
            pendingIntent = PendingIntent.getActivity(mContext, requestId, wrapIntent, flags);
        } catch (Exception e) {
        }
        return pendingIntent;
    }

    public PendingIntent getCancelPendingIntent(int notificationId) {
        PendingIntent pendingIntent = null;
        Intent wrapIntent = new Intent(mContext, BackMiddleActivity.class);
        wrapIntent.putExtra(CoreManager.EXTRA_NOTIFICATION_ID, notificationId);
        wrapIntent.putExtra(CoreManager.EXTRA_ONLY_CANCEL, true);
        int requestId = Long.valueOf(System.currentTimeMillis()).intValue() + 1002;
        try {
            int flags;
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            } else {
                flags = PendingIntent.FLAG_UPDATE_CURRENT;
            }
            pendingIntent = PendingIntent.getActivity(mContext, requestId, wrapIntent, flags);
        } catch (Exception e) {
        }
        return pendingIntent;
    }

    public String getTitleString(Params params) {
        String titleString = params.getTitleString();
        if (TextUtils.isEmpty(titleString)) {
            try {
                titleString = mContext.getResources().getString(params.getTitleId());
            } catch (Exception e) {
            }
        }
        return titleString;
    }

    public Bitmap getImageBitmap(RemindParams params) {
        Bitmap imageBitmap = params.getImageBitmap();
        if (imageBitmap == null) {
            try {
                imageBitmap = BitmapFactory.decodeResource(mContext.getResources(), params.getImageId());
            } catch (Exception e) {
            }
        }
        return imageBitmap;
    }

    public int getSmallIcon(Params params) {
        if (params != null && params.getSmallIcon() > 0) {
            return params.getSmallIcon();
        }
        try {
            return mContext.getApplicationInfo().icon;
        } catch (Exception e) {
        }
        return 0;
    }

    public Bitmap getIconBitmap(Params params) {
        Bitmap iconBitmap = params.getIconBitmap();
        if (iconBitmap == null) {
            try {
                iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), params.getIconId());
            } catch (Exception e) {
            }
        }
        return iconBitmap;
    }

    public String getDescString(Params params) {
        String descString = params.getDescString();
        if (TextUtils.isEmpty(descString)) {
            try {
                descString = mContext.getResources().getString(params.getDescId());
            } catch (Exception e) {
            }
        }
        return descString;
    }

    public String getActionString(Params params) {
        String actionString = params.getActionString();
        if (TextUtils.isEmpty(actionString)) {
            try {
                actionString = mContext.getResources().getString(params.getActionId());
            } catch (Exception e) {
            }
        }
        return actionString;
    }

    public void showRemind(SharpRemind.RemindMode remindMode) {
        if (remindMode == SharpRemind.RemindMode.ACTIVITY) {
            showRemindActivity();
            return;
        }
        if (remindMode == SharpRemind.RemindMode.NOTIFICATION) {
            showRemindNotification();
            return;
        }
        if (remindMode == SharpRemind.RemindMode.ACTIVITY_AND_NOTIFICATION) {
            showRemindNotification();
            showRemindActivity();
            return;
        }
    }

    private void showRemindActivity() {
        Intent intent = new Intent(mContext, RemindActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(CoreManager.EXTRA_NOTIFICATION_ID, getNotificationId());
        BackAct.startActivityBackground(mContext, intent);
        reportCallRemind();
    }

    private String getOnNotificationChannelId() {
        return mContext.getPackageName() + ".notification";
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = NotificationManagerCompat.from(mContext).getNotificationChannel(getOnNotificationChannelId());
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(getOnNotificationChannelId(), "daemon", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setSound(null, null);
                NotificationManagerCompat.from(mContext).createNotificationChannel(notificationChannel);
            }
        }
    }

    private void showRemindNotification() {
        createNotificationChannel();
        int notificationId = getNotificationId();
        RemindParams params = getRemindParams();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, getOnNotificationChannelId());
        builder.setSmallIcon(getSmallIcon(params));
        PendingIntent pendingIntent = getPendingIntent(params, false, notificationId);
        PendingIntent cancelPendingIntent = getCancelPendingIntent(notificationId);
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
        NotificationManagerCompat.from(mContext).notify(notificationId, notification);
        reportCallNotification();
    }

    private RemoteViews getRemindRemoteViews(RemindParams params, PendingIntent pendingIntent, PendingIntent cancelPendingIntent) {
        if (params == null) {
            Log.v(SharpRemind.TAG, "RemindParams is null");
            reportNotificationError("RemindParams is null");
            return null;
        }
        RemoteViews remoteViews = params.getRemoteViews();
        if (remoteViews != null) {
            return remoteViews;
        }
        int type = params.getNotificationLayout();
        if (type <= 0) {
            Log.v(SharpRemind.TAG, "LayoutType is not set, use default layout");
            type = RemindParams.LAYOUT_REMIND_1;
        }
        String titleString = getTitleString(params);
        if (TextUtils.isEmpty(titleString)) {
            Log.v(SharpRemind.TAG, "TitleString is not set");
            reportNotificationError("TitleString is not set");
            return null;
        }
        String descString = getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(SharpRemind.TAG, "DescString is not set");
            reportNotificationError("DescString is not set");
            return null;
        }
        String actionString = getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(SharpRemind.TAG, "ActionString is not set");
            reportNotificationError("ActionString is not set");
            return null;
        }
        Bitmap iconBitmap = getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(SharpRemind.TAG, "IconBitmap is not set");
            reportNotificationError("IconBitmap is not set");
            return null;
        }
        Bitmap imageBitmap = getImageBitmap(params);
        if (imageBitmap == null) {
            Log.v(SharpRemind.TAG, "ImageBitmap is not set");
            reportNotificationError("ImageBitmap is not set");
            return null;
        }
        remoteViews = new RemoteViews(mContext.getPackageName(), type);
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
        reportError(SharpRemind.RemindMode.NOTIFICATION, error);
    }

    private void registerActivityCallback() {
        try {
            if (mContext instanceof Application) {
                ((Application) mContext).unregisterActivityLifecycleCallbacks(this);
                ((Application) mContext).registerActivityLifecycleCallbacks(this);
            }
        } catch (Exception | Error e) {
            Log.e(SharpRemind.TAG, "error : " + e);
        }
    }

    public boolean appOnTop() {
        if (mAtomicInteger != null) {
            return mAtomicInteger.get() > 0;
        }
        return false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mAtomicInteger != null) {
            mAtomicInteger.incrementAndGet();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (mAtomicInteger != null) {
            mAtomicInteger.decrementAndGet();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private Bundle getParamsBundle(Params params) {
        if (params != null) {
            return params.getBundle();
        }
        return null;
    }

    public void reportCallRemind() {
        Log.v(SharpRemind.TAG, "report call remind");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportCallRemind(getParamsBundle(mRemindParams));
        }
    }

    public void reportCallNotification() {
        Log.v(SharpRemind.TAG, "report call notification");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportCallNotification(getParamsBundle(mRemindParams));
        }
    }

    public void reportShowRemind() {
        Log.v(SharpRemind.TAG, "report show remind");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportShowRemind(getParamsBundle(mRemindParams));
        }
    }

    public void reportRemindClick() {
        Log.v(SharpRemind.TAG, "report click remind");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportRemindClick(getParamsBundle(mRemindParams));
        }
    }

    public void reportShowOnGoing(int notificationId, Service service) {
        Log.v(SharpRemind.TAG, "report show ongoing");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportShowOnGoing(getParamsBundle(mOnGoingParams), notificationId, service);
        }
    }

    public void reportOnGoingClick() {
        Log.v(SharpRemind.TAG, "report click ongoing");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportOnGoingClick(getParamsBundle(mOnGoingParams));
        }
    }

    public void reportNotificationClick() {
        Log.v(SharpRemind.TAG, "report click notification");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportNotificationClick(getParamsBundle(mRemindParams));
        }
    }

    public void reportNotificationClose() {
        Log.v(SharpRemind.TAG, "report close notification");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportNotificationClose(getParamsBundle(mRemindParams));
        }
    }

    public void reportRemindClose() {
        Log.v(SharpRemind.TAG, "report close remind");
        if (mOnDataCallback != null) {
            mOnDataCallback.reportRemindClose(getParamsBundle(mRemindParams));
        }
    }

    public void reportError(SharpRemind.RemindMode remindMode, String error) {
        Log.v(SharpRemind.TAG, "mode : " + remindMode + " , error : " + error);
        if (mOnDataCallback != null) {
            mOnDataCallback.reportError(remindMode, error);
        }
    }
}
