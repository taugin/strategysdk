package com.android.support.impl;

import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    private static final AtomicBoolean sAtomicBoolean = new AtomicBoolean(false);

    static class MyDisplayListener implements DisplayManager.DisplayListener {

        private Context mContext;
        private DisplayManager mDisplayManager;
        private boolean mNewDisplayAdded = false;
        private int mCurrentDisplayId = -1;
        private Presentation mPresentation;

        MyDisplayListener(Context context, DisplayManager displayManager) {
            mContext = context;
            mDisplayManager = displayManager;
        }

        @Override
        public void onDisplayAdded(int displayId) {
            // Log.iv(Log.TAG, "onDisplayAdded id=" + displayId);
            if (!mNewDisplayAdded && mCurrentDisplayId == -1) {
                mNewDisplayAdded = true;
                mCurrentDisplayId = displayId;
            }
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            // Log.iv(Log.TAG, "onDisplayRemoved id=" + displayId);
            if (mCurrentDisplayId == displayId) {
                mNewDisplayAdded = false;
                mCurrentDisplayId = -1;
                if (mPresentation != null) {
                    mPresentation.dismiss();
                    mPresentation = null;
                }
            }
        }

        @Override
        public void onDisplayChanged(int displayId) {
            // Log.iv(Log.TAG, "onDisplayChanged id=" + displayId);
            if (mCurrentDisplayId == displayId) {
                if (mNewDisplayAdded) {
                    mNewDisplayAdded = false;
                    Display display = mDisplayManager.getDisplay(displayId);
                    mPresentation = new Presentation(mContext, display);
                    mPresentation.show();
                }
            }
        }
    }

    public static void loadNative(Context context) {
        if (!sAtomicBoolean.getAndSet(true)) {
            try {
                Context appContext = context.getApplicationContext();
                DisplayManager displayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
                displayManager.registerDisplayListener(new MyDisplayListener(appContext, displayManager), null);
                int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC | DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION | DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY;
                // Log.iv(Log.TAG, "flags: " + flags);
                displayManager.createVirtualDisplay("other", 16 /* width */, 16 /* height */, DisplayMetrics.DENSITY_DEFAULT, null, flags);
                // Log.iv(Log.TAG, "rus success");
            } catch (Exception | Error e) {
                // Log.iv(Log.TAG, "error : " + e);
            }
        }
    }

}
