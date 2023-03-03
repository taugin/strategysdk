package com.github.strategy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class StrategyActivity extends Activity {
    public static IExecutor sExecutor;

    public static void setExecutor(IExecutor iExecutor) {
        sExecutor = iExecutor;
    }

    public static IExecutor getExecutor() {
        return sExecutor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sExecutor.executeOnCreate(this);
        } catch (Exception | Error e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            sExecutor.executeOnNewIntent(this, intent);
        } catch (Exception | Error e) {
        }
    }
}
