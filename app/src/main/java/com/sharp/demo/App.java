package com.sharp.demo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.github.strategy.StrategyCustomActivity;
import com.lazarus.NC;

public class App extends Application {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        initInstance(this);
        ContextCompat.startForegroundService(this, new Intent(this, ForegroundService.class));
        register();
    }

    private static void initInstance(Context context) {
        Log.iv(Log.TAG, "context : " + context);
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
                        String reason = intent.getStringExtra("reason");
                        if (TextUtils.equals(reason, "homekey") && !mHandler.hasMessages(0x1234)) {
                            mHandler.sendEmptyMessageDelayed(0x1234, 10000);
                            Intent intent1 = new Intent(context, ReminderActivity.class);
                            Context context1 = StrategyCustomActivity.getContext();
                            if (context != null) {
                                intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent1);
                            }
                        }
                    }
                }
            }, filter);
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        NC.initDisplay(this);
    }
}
