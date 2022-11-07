package com.sharp.vdx.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.List;

public class BackActUtils {
    private static Handler sMainHandler = new Handler(Looper.getMainLooper());

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
                th.printStackTrace();
                return false;
            }
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK/*268435456*/);
            if (z) {
                int i = f18560b + 1;
                f18560b = i;
                try {
                    int flags;
                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        flags = PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE;
                    } else {
                        flags = PendingIntent.FLAG_ONE_SHOT;
                    }
                    PendingIntent.getActivity(context, i, intent, flags/*1073741824*/).send();
                    return true;
                } catch (Throwable th2) {
                    th2.printStackTrace();
                }
            }
            try {
                context.startActivity(intent);
                return true;
            } catch (Throwable th3) {
                th3.printStackTrace();
                return false;
            }
        }
    }

}
