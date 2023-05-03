package com.lazarus;

import android.content.Context;

import com.lazarus.log.Log;

public class Stat {
    public static void reportEvent(Context context, String eventId, String value) {
        try {
            String className = context.getApplicationInfo().className;
            Class.forName(className).getMethod("reportEvent", Context.class, String.class, String.class).invoke(null, context, eventId, value);
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }
}
