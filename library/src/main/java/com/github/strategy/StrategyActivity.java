package com.github.strategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class StrategyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ExecutorLoader.getExecutor().executeOnCreate(this);
        } catch (Exception | Error e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            ExecutorLoader.getExecutor().executeOnNewIntent(this, intent);
        } catch (Exception | Error e) {
        }
    }
}
