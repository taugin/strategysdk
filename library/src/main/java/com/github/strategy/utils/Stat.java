package com.github.strategy.utils;

import android.content.Context;

import com.github.strategy.log.Log;

public class Stat {
    public static void reportEvent(Context context, String eventId, String value) {
        try {
            String className = StrategyUtils.getStrategyActivityClassName(context);
            Class.forName(className).getMethod("reportEvent", Context.class, String.class, String.class).invoke(null, context, eventId, value);
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }
}
