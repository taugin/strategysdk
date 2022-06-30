package com.bigcow.backact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bigcow.backact.adapter.StartStrategyList;
import com.bigcow.backact.log.Log;
import com.bigcow.backact.utils.BackActUtils;

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
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
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
}
