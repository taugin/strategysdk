package com.vibrant.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.vibrant.VibrantRemind;
import com.vibrant.daemon.demo.R;
import com.vibrant.model.OnGoingParams;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OnGoingParams.Builder builder = new OnGoingParams.Builder();
        builder.setLayoutType(OnGoingParams.LAYOUT_ONGOING_1);
        builder.setSmallIcon(R.drawable.notify_icon_small);
        builder.setIconId(R.drawable.sp_icon);
        builder.setDescString("收服、训练和养成超过2000只怪兽，打造专属于你的队伍通往胜利之路！超大型的怪物对战RPG手游！");
        builder.setActionString("下载试玩");
        Bundle bundle = new Bundle();
        bundle.putString("start_app_from", String.valueOf(VibrantRemind.RemindMode.ONGOING));
        builder.setBundle(bundle);
        VibrantRemind.init(this, builder.build(), new VibrantRemind.OnDataCallback() {

            @Override
            public void reportCallRemind() {
                Log.v(Log.TAG, "report call remind");
            }

            @Override
            public void reportCallNotification() {
                Log.v(Log.TAG, "report call notification");
            }

            @Override
            public void reportShowRemind() {
                Log.v(Log.TAG, "report show remind");
            }

            @Override
            public void reportRemindClick() {
                Log.v(Log.TAG, "report click remind");
            }

            @Override
            public void reportOnGoingClick() {
                Log.v(Log.TAG, "report click ongoing");
            }

            @Override
            public void reportNotificationClick() {
                Log.v(Log.TAG, "report click notification");
            }

            @Override
            public void reportRemindClose() {
                Log.v(Log.TAG, "report close remind");
            }

            @Override
            public void reportError(VibrantRemind.RemindMode remindMode, String error) {
                Log.v(Log.TAG, "mode : " + remindMode + " , error : " + error);
            }
        });
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
