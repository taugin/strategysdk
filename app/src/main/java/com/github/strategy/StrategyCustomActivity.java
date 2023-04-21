package com.github.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Keep;

import com.sharp.demo.Log;


public class StrategyCustomActivity extends Activity {

    public static Context sCustomContext;

    @Keep
    public static void setContext(Context context) {
        sCustomContext = context;
    }

    public static Context getContext() {
        return sCustomContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getContext().sendBroadcast(getIntent().putExtra(Intent.EXTRA_REFERRER_NAME, "onCreate"));
            finish();
        } catch (Exception | Error e) {
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            setIntent(intent);
            getContext().sendBroadcast(getIntent().putExtra(Intent.EXTRA_REFERRER_NAME, "onNewIntent"));
            finish();
        } catch (Exception | Error e) {
        }
    }

    public static void reportEvent(Context context, String key, String value) {
        Log.iv(Log.TAG, "key : " + key + " , value : " + value);
    }
}
