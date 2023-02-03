package com.github.strategy.utils;

import android.content.Context;

import com.github.strategy.log.Log;

import java.util.Map;

public class Stat {
    public static void reportEvent(Context context, String eventId, String value, Map<String, Object> map) {
        Log.iv(Log.TAG, "eventId : " + eventId + " , value : " + value + " , map : " + map);
    }
}
