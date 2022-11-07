package com.sharp.demo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        register();
    }

    private void register() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (TextUtils.equals(action, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                        String extra = intent.getStringExtra("reason");
                        if (!TextUtils.equals(extra, "homekey")) {
                            return;
                        }
                        Vdx.startActivity(context, new Intent(context, ReminderActivity.class));
                    }
                }
            }, filter);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
    }
}
