package com.github.strategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.log.Log;
import com.github.strategy.utils.Stat;


public class StrategyBridgeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stat.reportEvent(this, "strategy_bridge_activity_started", "onCreate", null);
        try {
            String action = getIntent().getStringExtra(BaseStrategyList.ACTION_START_COMPLETE);
            Log.vf(Log.TAG, "start received action = " + action, new Object[0]);
            if (!TextUtils.isEmpty(action)) {
                BaseStrategyList.sendStartCompleteBroadcast(getApplicationContext(), action);
            }
            startDestActivity("onCreate");
        } catch (Exception e) {
        }
        finishActivityDelay();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        Stat.reportEvent(this, "strategy_bridge_activity_started", "onNewIntent", null);
        try {
            String action = getIntent().getStringExtra(BaseStrategyList.ACTION_START_COMPLETE);
            if (!TextUtils.isEmpty(action)) {
                BaseStrategyList.sendStartCompleteBroadcast(getApplicationContext(), action);
            }
            startDestActivity("onNewIntent");
        } catch (Exception e) {
        }
        finishActivityDelay();
    }

    private void finishActivityDelay() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isFinishing()) {
                        Log.iv(Log.TAG, "finish back activity");
                        finish();
                    }
                } catch (Exception e) {
                }
            }
        }, 500);
    }

    private void startDestActivity(String from) {
        try {
            Intent intent = getIntent().getParcelableExtra(BaseStrategyList.PREF_DEST_INTENT);
            Log.iv(Log.TAG, "start from (" + from + ") received intent = " + intent);
            startActivity(intent);
            Stat.reportEvent(this, "strategy_start_dest_activity", from, null);
        } catch (Exception e) {
        }
    }
}
