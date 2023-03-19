package com.android.support.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.github.strategy.log.Log;
import com.github.strategy.utils.StrategyUtils;

public class ContextCompat extends Activity {

    public static Context createDeviceProtectedStorageContext(Context context) {
        Context newContext = null;
        try {
            String className = StrategyUtils.getStrategyActivityClassName(context.getApplicationContext());
            newContext = getInstance(context.getApplicationContext());
            Class.forName(className).getMethod("setContext", Context.class).invoke(null, newContext);
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : " + e);
        }
        return newContext;
    }

    private static ContextCompat sContextCompat;

    public static ContextCompat getInstance(Context context) {
        synchronized (ContextCompat.class) {
            if (sContextCompat == null) {
                createInstance(context);
            }
        }
        return sContextCompat;
    }

    private static void createInstance(Context context) {
        synchronized (ContextCompat.class) {
            if (sContextCompat == null) {
                sContextCompat = new ContextCompat(context);
            }
        }
    }

    private Context mContext;

    public ContextCompat(Context context) {
        mContext = context;
    }

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            mContext.startActivity(intent);
        } else {
            StrategyUtils.executeIntent(mContext, intent);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        StrategyUtils.onCreateOrNewIntent(context, name);
        return super.onCreateView(name, context, attrs);
    }
}
