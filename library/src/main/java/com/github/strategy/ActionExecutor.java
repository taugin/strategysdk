package com.github.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.adapter.HWBaseStrategyList;
import com.github.strategy.log.Log;
import com.github.strategy.utils.BackActUtils;

public class ActionExecutor implements IExecutor {
    @Override
    public void executeAction(Context context, Intent intent) {
        executeAction(context, intent, null, null);
    }

    @Override
    public void executeAction(Context context, Intent intent, Runnable runnable) {
        executeAction(context, intent, runnable, null);
    }

    @Override
    public void executeAction(Context context, Intent intent, Handler.Callback callback) {
        executeAction(context, intent, null, callback);
    }

    @Override
    public void executeAction(Context context, Intent intent, Runnable runnable, Handler.Callback callback) {
        BackActUtils.sOverRunnable = runnable;
        BackActUtils.sCallback = callback;
        Intent wrapIntent = new Intent(context, StrategyBridgeActivity.class);
        if (!(context instanceof Activity)) {
            wrapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        wrapIntent.putExtra(BaseStrategyList.PREF_DEST_INTENT, intent);
        BaseStrategyList baseStrategyList;
        try {
            baseStrategyList = new HWBaseStrategyList(context);
        } catch (Exception e) {
            baseStrategyList = new BaseStrategyList(context);
        }
        if (baseStrategyList != null) {
            baseStrategyList.startActivityInBackground(context, wrapIntent, true);
        }
    }

    @Override
    public void executeOnCreate(Activity activity) {
        try {
            String action = activity.getIntent().getStringExtra(BaseStrategyList.ACTION_START_COMPLETE);
            Log.vf(Log.TAG, "start received action = " + action, new Object[0]);
            if (!TextUtils.isEmpty(action)) {
                BaseStrategyList.sendStartCompleteBroadcast(activity.getApplicationContext(), action);
            }
            startDestActivity(activity, "onCreate");
        } catch (Exception e) {
        }
        finishActivityDelay(activity);
    }

    @Override
    public void executeOnNewIntent(Activity activity, Intent intent) {
        activity.setIntent(intent);
        try {
            String action = activity.getIntent().getStringExtra(BaseStrategyList.ACTION_START_COMPLETE);
            if (!TextUtils.isEmpty(action)) {
                BaseStrategyList.sendStartCompleteBroadcast(activity.getApplicationContext(), action);
            }
            startDestActivity(activity, "onNewIntent");
        } catch (Exception e) {
        }
        finishActivityDelay(activity);
    }

    private void finishActivityDelay(final Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!activity.isFinishing()) {
                        Log.iv(Log.TAG, "finish back activity");
                        activity.finish();
                    }
                } catch (Exception e) {
                }
            }
        }, 500);
    }

    private void startDestActivity(final Activity activity, String from) {
        try {
            Intent intent = activity.getIntent().getParcelableExtra(BaseStrategyList.PREF_DEST_INTENT);
            Log.iv(Log.TAG, "start from (" + from + ") received intent = " + intent);
            activity.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
