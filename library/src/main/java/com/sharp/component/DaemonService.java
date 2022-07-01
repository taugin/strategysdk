package com.sharp.component;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class DaemonService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        try {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ResidentService.class.getName());
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } catch (Exception e) {
        }
        Intent assistIntent1 = new Intent();
        assistIntent1.setClassName(getPackageName(), AssistService1.class.getName());
        startService(assistIntent1);

        Intent assistIntent2 = new Intent();
        assistIntent2.setClassName(getPackageName(), AssistService2.class.getName());
        startService(assistIntent2);
    }
}
