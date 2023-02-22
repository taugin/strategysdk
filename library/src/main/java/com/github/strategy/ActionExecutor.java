package com.github.strategy;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

import com.github.strategy.adapter.BaseStrategyList;
import com.github.strategy.adapter.HWBaseStrategyList;
import com.github.strategy.strategy.IStartStrategy;
import com.github.strategy.utils.BackActUtils;


public class ActionExecutor {
    public static void executeAction(Context context, Intent intent) {
        executeAction(context, intent, null, null, null);
    }

    public static void executeAction(Context context, Intent intent, Runnable runnable) {
        executeAction(context, intent, null, runnable, null);
    }

    public static void executeAction(Context context, Intent intent, Handler.Callback callback) {
        executeAction(context, intent, null, null, callback);
    }

    public static void executeAction(Context context, Intent intent, RemoteExtra remoteExtra) {
        executeAction(context, intent, remoteExtra, null, null);
    }

    public static void executeAction(Context context, Intent intent, RemoteExtra remoteExtra, Runnable runnable, Handler.Callback callback) {
        if (remoteExtra != null) {
            intent.putExtra(IStartStrategy.EXTRA_REMOVE_VIEWS, remoteExtra.remoteViews);
            intent.putExtra(IStartStrategy.EXTRA_FULLSCREEN_PENDING_INTENT, remoteExtra.fullscreenIntent);
            intent.putExtra(IStartStrategy.EXTRA_NOTIFICATION_ID, remoteExtra.notificationId);
            intent.putExtra(IStartStrategy.EXTRA_CANCEL_ALL, remoteExtra.cancelAll);
        }
        BackActUtils.sOverRunnable = runnable;
        BackActUtils.sCallback = callback;
        Intent wrapIntent = new Intent(context, StrategyBridgeActivity.class);
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

    public static class RemoteExtra {
        public RemoteViews remoteViews;
        public int notificationId;
        public PendingIntent fullscreenIntent;
        public boolean cancelAll;

        public RemoteExtra(RemoteViews remoteViews, int notificationId, PendingIntent fullscreenIntent) {
            this.remoteViews = remoteViews;
            this.notificationId = notificationId;
            this.fullscreenIntent = fullscreenIntent;
        }

        public void setCancelAll(boolean cancelAll) {
            this.cancelAll = cancelAll;
        }
    }
}
