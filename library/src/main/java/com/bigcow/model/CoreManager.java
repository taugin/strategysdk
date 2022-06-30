package com.bigcow.model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.bigcow.backact.BackMainActivity;
import com.bigcow.backact.adapter.StartStrategyList;

public class CoreManager {
    public static final String EXTRA_SHOW_REMIND = "extra_show_remind";
    public static final String EXTRA_REMIND_MODE = "extra_show_notification";
    public static final String EXTRA_NOTIFICATION_ID = "extra_notification_id";
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
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OnGoingParams mOnGoingParams;
    private RemindParams mRemindParams;

    private CoreManager(Context context) {
        mContext = context.getApplicationContext();
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
        if (params != null && params.getBundle() != null && intent != null) {
            intent.putExtras(params.getBundle());
        }
        return intent;
    }

    public int getRemindId() {
        return 0x1234;
    }

    public PendingIntent getPendingIntent(Params params) {
        PendingIntent pendingIntent = null;
        Intent intent = getDestIntent(params);
        Intent wrapIntent = new Intent(mContext, BackMainActivity.class);
        wrapIntent.putExtra(StartStrategyList.PREF_DEST_INTENT, intent);
        wrapIntent.putExtra(CoreManager.EXTRA_NOTIFICATION_ID, getRemindId());
        int requestId = Long.valueOf(System.currentTimeMillis()).intValue() + 1001;
        try {
            pendingIntent = PendingIntent.getActivity(mContext, requestId, wrapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
        }
        return pendingIntent;
    }

    public String getTitleString(RemindParams params) {
        String titleString = params.getTitleString();
        if (TextUtils.isEmpty(titleString)) {
            titleString = mContext.getResources().getString(params.getTitleId());
        }
        return titleString;
    }

    public Bitmap getImageBitmap(RemindParams params) {
        Bitmap imageBitmap = params.getImageBitmap();
        if (imageBitmap == null) {
            imageBitmap = BitmapFactory.decodeResource(mContext.getResources(), params.getImageId());
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
            iconBitmap = BitmapFactory.decodeResource(mContext.getResources(), params.getIconId());
        }
        return iconBitmap;
    }

    public String getDescString(Params params) {
        String descString = params.getDescString();
        if (TextUtils.isEmpty(descString)) {
            descString = mContext.getResources().getString(params.getDescId());
        }
        return descString;
    }

    public String getActionString(Params params) {
        String actionString = params.getActionString();
        if (TextUtils.isEmpty(actionString)) {
            actionString = mContext.getResources().getString(params.getActionId());
        }
        return actionString;
    }
}
