package com.android.support.content;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.github.strategy.log.Log;
import com.github.strategy.utils.StrategyUtils;

import java.lang.reflect.Method;

public class MainApplication extends Application {

    public static Context createDeviceProtectedStorageContext(Context context) {
        Context newContext = null;
        try {
            newContext = getInstance(context.getApplicationContext());
        } catch (Exception e) {
        }
        setApplicationContext(context, newContext);
        setActivityContext(context, newContext);
        return newContext;
    }

    private static void setApplicationContext(Context context, Context newContext) {
        try {
            String appClassName = context.getApplicationInfo().className;
            Method method = Class.forName(appClassName).getDeclaredMethod("initInstance", Context.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(null, newContext);
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : init instance");
        }
    }

    private static void setActivityContext(Context context, Context newContext) {
        try {
            String className = StrategyUtils.getStrategyActivityClassName(context.getApplicationContext());
            Class.forName(className).getMethod("setContext", Context.class).invoke(null, newContext);
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : set context");
        }
    }

    private static MainApplication sMainApplication;

    public static MainApplication getInstance(Context context) {
        synchronized (MainApplication.class) {
            if (sMainApplication == null) {
                createInstance(context);
            }
        }
        return sMainApplication;
    }

    private static void createInstance(Context context) {
        synchronized (MainApplication.class) {
            if (sMainApplication == null) {
                sMainApplication = new MainApplication(context);
            }
        }
    }

    private Context mContext;

    public MainApplication(Context context) {
        mContext = context != null ? context.getApplicationContext() : null;
        try {
            attachBaseContext(mContext);
        } catch (Exception | Error e) {
        }
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
    public void sendBroadcast(Intent intent) {
        try {
            Intent newIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (newIntent != null) {
                StrategyUtils.onCreateOrNewIntent(mContext, intent);
            } else {
                super.sendBroadcast(intent);
            }
        } catch (Exception e) {
            super.sendBroadcast(intent);
        }
    }
}
