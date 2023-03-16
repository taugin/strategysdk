package com.github.strategy.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.strategy.StrategyActivity;
import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.adapter.HWBaseStrategyList;
import com.github.strategy.log.Log;

import java.util.List;

public class StrategyUtils {
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    public static void postRunnableDelay(Runnable runnable, long delay) {
        sMainHandler.postDelayed(runnable, delay);
    }

    public static boolean isInForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                if (runningAppProcesses != null) {
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                        if (TextUtils.equals(context.getApplicationInfo().processName, runningAppProcessInfo.processName) && runningAppProcessInfo.importance == 100) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public static void moveToFront(Context context) {
        List<ActivityManager.RunningTaskInfo> runningTasks;
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (!(activityManager == null || (runningTasks = activityManager.getRunningTasks(10)) == null)) {
                for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                    ComponentName componentName = runningTaskInfo.topActivity;
                    if (componentName != null && componentName.getPackageName().equals(context.getPackageName())) {
                        activityManager.moveTaskToFront(runningTaskInfo.id, 0);
                        return;
                    }
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static int f18560b = 10;

    public static boolean startActivityOrUsePending(Context context, Intent intent) {
        return startActivityOrUsePending(context, intent, true);
    }

    public static boolean startActivityOrUsePending(Context context, Intent intent, boolean z) {
        if (context instanceof Activity) {
            try {
                context.startActivity(intent);
                return true;
            } catch (Throwable th) {
                Log.iv(Log.TAG, "error : " + th);
                return false;
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK/*268435456*/);
            if (z) {
                int i = f18560b + 1;
                f18560b = i;
                try {
                    PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE/*1073741824*/).send();
                    return true;
                } catch (Throwable th2) {
                    Log.iv(Log.TAG, "error : " + th2);
                }
            }
            try {
                context.startActivity(intent);
                return true;
            } catch (Throwable th3) {
                Log.iv(Log.TAG, "error : " + th3);
                return false;
            }
        }
    }

    public static void onCreateOrNewIntent(Context context, String name) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            try {
                String action = activity.getIntent().getStringExtra(BaseStrategyList.ACTION_START_COMPLETE);
                Log.vf(Log.TAG, "start received action = " + action, new Object[0]);
                if (!TextUtils.isEmpty(action)) {
                    BaseStrategyList.sendStartCompleteBroadcast(activity.getApplicationContext(), action);
                }
                startDestActivity(activity, name);
            } catch (Exception e) {
            }
            finishActivityDelay(activity);
        }
    }


    private static void finishActivityDelay(final Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!activity.isFinishing()) {
                        Log.iv(Log.TAG, "finish back activity");
                        activity.finish();
                    }
                } catch (Exception e) {
                }
            }
        }, 500);
    }

    private static void startDestActivity(final Activity activity, String from) {
        try {
            Intent intent = activity.getIntent().getParcelableExtra(BaseStrategyList.PREF_DEST_INTENT);
            Log.iv(Log.TAG, "start from (" + from + ") received intent = " + intent);
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public static String getStrategyActivityClassName(Context context) {
        try {
            String action = context.getPackageName() + ".intent.action.QUICK_VIEW";
            Intent queryIntent = new Intent(action);
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(queryIntent, 0);
            if (list != null && !list.isEmpty()) {
                return list.get(0).activityInfo.name;
            }
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        return null;
    }

    public static void executeIntent(Context context, Intent intent) {
        Intent wrapIntent = new Intent();
        String className = StrategyUtils.getStrategyActivityClassName(context);
        if (TextUtils.isEmpty(className)) {
            className = StrategyActivity.class.getName();
        }
        wrapIntent.setClassName(context.getPackageName(), className);
        if (!(context instanceof Activity)) {
            wrapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        wrapIntent.putExtra(BaseStrategyList.PREF_DEST_INTENT, intent);
        BaseStrategyList baseStrategyList;
        try {
            baseStrategyList = new HWBaseStrategyList(context);
        } catch (Exception e) {
            baseStrategyList = new BaseStrategyList(context);
        }
        if (baseStrategyList != null) {
            baseStrategyList.startActivityInBackground(context, wrapIntent, true);
        }
    }

    public static void executeOverAction(Context context, Intent intent) {
        if (context != null && intent != null) {
            Intent destIntent = intent.getParcelableExtra(BaseStrategyList.PREF_DEST_INTENT);
            if (destIntent != null) {
                Notification notification = destIntent.getParcelableExtra(Intent.EXTRA_REPLACEMENT_EXTRAS);
                Log.iv(Log.TAG, "over action notification : " + notification);
                if (notification != null) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.notify(0x1987, notification);
                    }
                }
            }
        }
    }
}
