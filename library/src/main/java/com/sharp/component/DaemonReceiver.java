package com.sharp.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sharp.log.Log;

public class DaemonReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.v(Log.TAG, "onReceiver");
    }
}
