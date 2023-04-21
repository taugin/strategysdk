package com.github.strategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.support.content.MainApplication;
import com.github.strategy.utils.StrategyUtils;


public class StrategyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            MainApplication.getInstance(this).sendBroadcast(getIntent().putExtra(Intent.EXTRA_REFERRER_NAME, "onCreate"));
            StrategyUtils.finishActivityDelay(this);
        } catch (Exception | Error e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            setIntent(intent);
            MainApplication.getInstance(this).sendBroadcast(getIntent().putExtra(Intent.EXTRA_REFERRER_NAME, "onNewIntent"));
            StrategyUtils.finishActivityDelay(this);
        } catch (Exception | Error e) {
        }
    }
}
