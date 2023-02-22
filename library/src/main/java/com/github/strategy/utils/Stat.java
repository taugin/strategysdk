package com.github.strategy.utils;

import android.content.Context;
import android.os.Message;
import android.util.Pair;

public class Stat {
    public static void reportEvent(Context context, String eventId, String value) {
        if (BackActUtils.sCallback != null) {
            Message message = Message.obtain();
            message.obj = new Pair<String, String>(eventId, value);
            BackActUtils.sCallback.handleMessage(message);
        }
    }
}
