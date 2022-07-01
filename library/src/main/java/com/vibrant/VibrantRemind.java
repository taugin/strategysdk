package com.vibrant;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.core.content.ContextCompat;

import com.vibrant.component.DaemonInstrumentation;
import com.vibrant.component.DaemonReceiver;
import com.vibrant.component.DaemonService;
import com.vibrant.component.RemindFutureService;
import com.vibrant.daemon.JavaDaemon;
import com.vibrant.model.CoreManager;
import com.vibrant.model.OnGoingParams;
import com.vibrant.model.RemindParams;

public class VibrantRemind {
    public enum RemindMode {
        ACTIVITY, NOTIFICATION, ACTIVITY_AND_NOTIFICATION, ONGOING
    }

    public static void init(Context context, OnGoingParams params) {
        init(context, params, null);
    }

    public static void init(Context context, OnGoingParams params, OnDataCallback callback) {
        CoreManager.get(context).init();
        CoreManager.get(context).setOnDataCallback(callback);
        CoreManager.get(context).setOnGoingParams(params);
        JavaDaemon.getInstance().init(context, new Intent(context, DaemonService.class), new Intent(context, DaemonReceiver.class), new Intent(context, DaemonInstrumentation.class));
        JavaDaemon.getInstance().startAppLock(context, new String[]{"daemon", "assist1", "assist2"});
        if (isMainProcess(context)) {
            startResidentService(context);
        }
    }

    public static void showRemind(Context context, RemindParams params) {
        showRemind(context, params, RemindMode.ACTIVITY);
    }

    public static void showRemind(Context context, RemindParams params, RemindMode remindMode) {
        CoreManager.get(context).setRemindParams(params);
        Intent intent = new Intent(context, RemindFutureService.class);
        intent.putExtra(CoreManager.EXTRA_SHOW_REMIND, true);
        intent.putExtra(CoreManager.EXTRA_REMIND_MODE, remindMode);
        ContextCompat.startForegroundService(context, intent);
    }

    private static void startResidentService(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    ContextCompat.startForegroundService(context, new Intent(context, RemindFutureService.class));
                    return false;
                }
            });
        } else {
            ContextCompat.startForegroundService(context, new Intent(context, RemindFutureService.class));
        }
    }

    private static boolean isMainProcess(Context context) {
        try {
            return context.getPackageName().equals(getCurrentProcessName(context));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 获取当前进程名
     */
    private static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (null != manager) {
                for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                    if (process.pid == pid) {
                        processName = process.processName;
                    }
                }
            }
        } catch (Exception e) {
        }
        return processName;
    }

    public static abstract class OnDataCallback {
        public void reportCallRemind(Bundle bundle) {
        }

        public void reportCallNotification(Bundle bundle) {
        }

        public void reportShowRemind(Bundle bundle) {
        }

        public void reportRemindClick(Bundle bundle) {
        }

        public void reportOnGoingClick(Bundle bundle) {
        }

        public void reportNotificationClick(Bundle bundle) {
        }

        public void reportNotificationClose(Bundle bundle) {
        }

        public void reportRemindClose(Bundle bundle) {
        }

        public void reportError(VibrantRemind.RemindMode remindMode, String error) {
        }
    }
}
