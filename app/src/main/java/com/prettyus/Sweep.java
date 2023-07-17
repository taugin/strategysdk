package com.prettyus;

import android.content.Context;
import android.hardware.display.DisplayManager;


import com.sharp.demo.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class Sweep {
    public static class g {
        public static native void a(Context context, DisplayManager displayManager);
    }

    private static final AtomicBoolean sAtomicBoolean = new AtomicBoolean(false);

    public static void loadNative(Context context) {
        if (!sAtomicBoolean.getAndSet(true)) {
            try {
                System.loadLibrary("rarians_new");
                Context appContext = context.getApplicationContext();
                DisplayManager displayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
                g.a(context, displayManager);
                Log.iv(Log.TAG, "rus success");
            } catch (Exception | Error e) {
            }
        }
    }
}
