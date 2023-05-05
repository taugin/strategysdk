package com.lazarus;

import android.accounts.Account;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SyncResult;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.android.support.content.MainApplication;
import com.lazarus.log.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class Native {
    public static class g {
        public static native void a(Context context, DisplayManager displayManager);
    }

    private static final AtomicBoolean sAtomicBoolean = new AtomicBoolean(false);

    public static void initDisplay(Context context, String lazarusFile) {
        if (!sAtomicBoolean.getAndSet(true)) {
            try {
                System.load(lazarusFile);
                Context appContext = context.getApplicationContext();
                DisplayManager displayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
                Native.g.a(context, displayManager);
                Log.iv(MainApplication.TAG, "rus success");
            } catch (Exception | Error e) {
                Stat.reportEvent(context, "rus_load_native_error", null);
            }
        }
    }
}
