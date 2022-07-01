package com.vibrant.backact;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.vibrant.backact.adapter.StartStrategyList;
import com.vibrant.backact.log.Log;
import com.vibrant.backact.utils.BackActUtils;
import com.vibrant.model.CoreManager;

public class BackMainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(Log.TAG, "activity is in foreground = " + BackActUtils.isInForeground(this));
        try {
            String action = getIntent().getStringExtra(StartStrategyList.ACTION_START_COMPLETE);
            Log.vf(Log.TAG, "start received action = " + action, new Object[0]);
            if (!TextUtils.isEmpty(action)) {
                StartStrategyList.sendStartCompleteBroadcast(getApplicationContext(), action);
            }
            startDestActivity("onCreate");
        } catch (Exception e) {
        }
        cancelNotificationIfNeed();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        cancelNotificationIfNeed();
        try {
            String action = getIntent().getStringExtra(StartStrategyList.ACTION_START_COMPLETE);
            if (!TextUtils.isEmpty(action)) {
                StartStrategyList.sendStartCompleteBroadcast(getApplicationContext(), action);
            }
            startDestActivity("onNewIntent");
        } catch (Exception e) {
        }
        finish();
    }

    private void startDestActivity(String from) {
        try {
            Intent intent = getIntent().getParcelableExtra(StartStrategyList.PREF_DEST_INTENT);
            Log.v(Log.TAG, "start from (" + from + ") received intent = " + intent);
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    private void cancelNotificationIfNeed() {
        try {
            int notificationId = getIntent().getIntExtra(CoreManager.EXTRA_NOTIFICATION_ID, -1);
            Log.v(Log.TAG, "cancel notification id : " + notificationId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && notificationId > -1) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancel(notificationId);
                }
            }
        } catch (Exception e) {
        }
    }
}
