package com.github.strategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class StrategyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ContextImpl.getInstance(this).onCreateView("onCreate", this, null);
        } catch (Exception | Error e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            setIntent(intent);
            ContextImpl.getInstance(this).onCreateView("onNewIntent", this, null);
        } catch (Exception | Error e) {
        }
    }
}
