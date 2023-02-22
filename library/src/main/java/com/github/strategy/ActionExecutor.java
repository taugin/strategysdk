package com.github.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.adapter.HWBaseStrategyList;
import com.github.strategy.utils.BackActUtils;


public class ActionExecutor {
    public static void executeAction(Context context, Intent intent) {
        executeAction(context, intent, null, null);
    }

    public static void executeAction(Context context, Intent intent, Runnable runnable) {
        executeAction(context, intent, runnable, null);
    }

    public static void executeAction(Context context, Intent intent, Handler.Callback callback) {
        executeAction(context, intent, null, callback);
    }

    public static void executeAction(Context context, Intent intent, Runnable runnable, Handler.Callback callback) {
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
}
