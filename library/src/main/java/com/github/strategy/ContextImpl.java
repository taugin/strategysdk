package com.github.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.github.strategy.log.Log;
import com.github.strategy.utils.StrategyUtils;

public class ContextImpl extends Activity {

    public static Context init(Context context) {
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

    private static ContextImpl sContextImpl;

    public static ContextImpl getInstance(Context context) {
        synchronized (ContextImpl.class) {
            if (sContextImpl == null) {
                createInstance(context);
            }
        }
        return sContextImpl;
    }

    private static void createInstance(Context context) {
        synchronized (ContextImpl.class) {
            if (sContextImpl == null) {
                sContextImpl = new ContextImpl(context);
            }
        }
    }

    private Context mContext;

    public ContextImpl(Context context) {
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
