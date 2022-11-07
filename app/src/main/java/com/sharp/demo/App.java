package com.sharp.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String bundleToString(Bundle bundle) {
        if (bundle != null) {
            Set<String> set = bundle.keySet();
            Map<String, Object> map = new HashMap<>();
            if (set != null) {
                for (String s : set) {
                    map.put(s, bundle.get(s));
                }
            }
            return map.toString();
        }
        return "";
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public boolean isMainProcess() {
        return getApplicationContext().getPackageName().equals(getCurrentProcessName());
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (null != manager) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        }
        return processName;
    }
}
