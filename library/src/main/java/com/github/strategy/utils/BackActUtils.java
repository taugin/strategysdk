package com.github.strategy.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.strategy.log.Log;

import java.util.List;

public class BackActUtils {
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());
    public static Runnable sOverRunnable;

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

}
