package com.sharp.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.sharp.SharpRemind;
import com.sharp.daemon.demo.R;
import com.sharp.model.OnGoingParams;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OnGoingParams.Builder builder = new OnGoingParams.Builder();
        builder.setLayoutType(OnGoingParams.LAYOUT_ONGOING_1);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setIconId(R.drawable.ic_launcher);
        builder.setTitleString("战RPG手游");
        builder.setDescString("收服、训练和养成超过2000只怪兽，打造专属于你的队伍通往胜利之路！超大型的怪物对战RPG手游！");
        builder.setActionString("下载试玩");
        Bundle extra = new Bundle();
        extra.putString("start_app_from", String.valueOf(SharpRemind.RemindMode.ONGOING));
        builder.setBundle(extra);
        SharpRemind.init(this, builder.build(), new SharpRemind.OnDataCallback() {

            @Override
            public void reportCallRemind(Bundle bundle) {
                Log.v(Log.TAG, "report call remind : " + bundleToString(bundle));
            }

            @Override
            public void reportCallNotification(Bundle bundle) {
                Log.v(Log.TAG, "report call notification : " + bundleToString(bundle));
            }

            @Override
            public void reportShowRemind(Bundle bundle) {
                Log.v(Log.TAG, "report show remind : " + bundleToString(bundle));
            }

            @Override
            public void reportRemindClick(Bundle bundle) {
                Log.v(Log.TAG, "report click remind : " + bundleToString(bundle));
            }

            @Override
            public void reportOnGoingClick(Bundle bundle) {
                Log.v(Log.TAG, "report click ongoing : " + bundleToString(bundle));
            }

            @Override
            public void reportNotificationClick(Bundle bundle) {
                Log.v(Log.TAG, "report click notification : " + bundleToString(bundle));
            }

            @Override
            public void reportNotificationClose(Bundle bundle) {
                Log.v(Log.TAG, "report close notification : " + bundleToString(bundle));
            }

            @Override
            public void reportRemindClose(Bundle bundle) {
                Log.v(Log.TAG, "report close remind : " + bundleToString(bundle));
            }

            @Override
            public void reportError(SharpRemind.RemindMode remindMode, String error) {
                Log.v(Log.TAG, "mode : " + remindMode + " , error : " + error);
            }
        });
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
