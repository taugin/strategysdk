package com.bigcow;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.core.content.ContextCompat;

import com.bigcow.component.BigcowFutureService;
import com.bigcow.component.DaemonInstrumentation;
import com.bigcow.component.DaemonReceiver;
import com.bigcow.component.DaemonService;
import com.bigcow.daemon.JavaDaemon;
import com.bigcow.model.CoreManager;
import com.bigcow.model.OnGoingParams;
import com.bigcow.model.RemindParams;

public class KeepAlive {
    public static enum RemindMode {
        ACTIVITY, NOTIFICATION, ACTIVITY_AND_NOTIFICATION
    }
    public static void attachBaseContext(Context context, OnGoingParams params) {
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
        Intent intent = new Intent(context, BigcowFutureService.class);
        intent.putExtra(CoreManager.EXTRA_SHOW_REMIND, true);
        intent.putExtra(CoreManager.EXTRA_REMIND_MODE, remindMode);
        ContextCompat.startForegroundService(context, intent);
    }

    private static void startResidentService(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final long time1 = System.currentTimeMillis();
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    long time2 = System.currentTimeMillis();
                    ContextCompat.startForegroundService(context, new Intent(context, BigcowFutureService.class));
                    return false;
                }
            });
        } else {
            ContextCompat.startForegroundService(context, new Intent(context, BigcowFutureService.class));
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
}
