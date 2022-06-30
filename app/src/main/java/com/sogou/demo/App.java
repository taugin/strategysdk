package com.sogou.demo;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.bigcow.KeepAlive;
import com.bigcow.model.OnGoingParams;
import com.sogou.daemon.demo.R;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        OnGoingParams.Builder builder = new OnGoingParams.Builder();
        builder.setLayoutType(OnGoingParams.LAYOUT_TYPE_1);
        builder.setSmallIcon(R.drawable.notify_icon_small);
        builder.setIconId(R.drawable.sp_icon);
        builder.setDescString("收服、训练和养成超过2000只怪兽，打造专属于你的队伍通往胜利之路！超大型的怪物对战RPG手游！");
        builder.setActionString("下载试玩");
        Bundle bundle = new Bundle();
        bundle.putString("test_bundle_key", "test bundle value");
        builder.setBundle(bundle);
        KeepAlive.attachBaseContext(this, builder.build());
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
