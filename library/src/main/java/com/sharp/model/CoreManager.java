package com.sharp.model;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sharp.SharpRemind;
import com.sharp.log.Log;
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
        if (sCoreManager != null) {
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
            pendingIntent = PendingIntent.getActivity(mContext, requestId, wrapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
            pendingIntent = PendingIntent.getActivity(mContext, requestId, wrapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
