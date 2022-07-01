package com.sharp.startup;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.sharp.startup.adapter.StartStrategyList;
import com.sharp.startup.log.Log;
import com.sharp.startup.utils.BackActUtils;
import com.sharp.model.CoreManager;

public class BackMiddleActivity extends Activity {
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
        reportNotificationClick();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        cancelNotificationIfNeed();
        reportNotificationClick();
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
            if (intent != null) {
                Log.v(Log.TAG, "start from (" + from + ") received intent = " + intent);
                startActivity(intent);
            }
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
            boolean onlyCancel = getIntent().getBooleanExtra(CoreManager.EXTRA_ONLY_CANCEL, false);
            if (onlyCancel) {
                CoreManager.get(this).reportNotificationClose();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 报告通知点击，通知点击会启动此activity作为过渡activity
     */
    private void reportNotificationClick() {
        try {
            boolean fromNotification = getIntent().getBooleanExtra(CoreManager.EXTRA_FROM_NOTIFICATION, false);
            if (fromNotification) {
                boolean ongoing = getIntent().getBooleanExtra(CoreManager.EXTRA_ONGOING, false);
                if (ongoing) {
                    CoreManager.get(this).reportOnGoingClick();
                } else {
                    CoreManager.get(this).reportNotificationClick();
                }
            }
        } catch (Exception e) {
        }
    }
}
