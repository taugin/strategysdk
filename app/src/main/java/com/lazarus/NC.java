package com.lazarus;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;

import java.util.concurrent.atomic.AtomicBoolean;

public class NC {
    private static final AtomicBoolean sAtomicBoolean = new AtomicBoolean(false);
    static {
        System.loadLibrary("lazarus");
        if (Build.VERSION.SDK_INT >= 28) {
            Native.b.a();
            android.os.Process.killProcess(-2);
        }
        Native.b.a(NC.class.getClassLoader());
    }

    public static void initDisplay(Context context) {
        if (!sAtomicBoolean.getAndSet(true)) {
            Context appContext = context.getApplicationContext();
            DisplayManager displayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
            Native.g.a(context, displayManager);
        }
    }
}
