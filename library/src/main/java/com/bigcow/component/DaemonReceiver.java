package com.bigcow.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bigcow.log.Log;

public class DaemonReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.v(Log.TAG, "onReceiver");
    }
}
