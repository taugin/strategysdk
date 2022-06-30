package com.bigcow.backact.strategy;

import android.content.Context;
import android.content.Intent;

import com.bigcow.backact.log.Log;
import com.bigcow.backact.utils.BackActUtils;


public class StartStrategyBringForegroundImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 1500;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        boolean isTop = BackActUtils.isInForeground(context);
        Log.v(Log.TAG, "vivo isAppRunningForeground = " + isTop);
        if (isTop) {
            return true;
        }
        for (int i = 0; i < 10; i++) {
            try {
                BackActUtils.moveToFront(context);
                Thread.sleep(50);
                if (BackActUtils.isInForeground(context)) {
                    BackActUtils.startActivityOrUsePending(context, intent);
                }
            } catch (Exception unused) {
            }
        }
        return true;

    }

    @Override
    public String getName() {
        return "safe";
    }
}
