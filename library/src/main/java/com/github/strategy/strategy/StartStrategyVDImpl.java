package com.github.strategy.strategy;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

import com.github.strategy.log.Log;
import com.github.strategy.utils.StrategyUtils;

import java.lang.reflect.Method;


public class StartStrategyVDImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 4000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        try {
            Object displayObject = createDisplay(context);
            if (displayObject instanceof Display) {
                new VMPresentation(context, (Display) displayObject).show();
                StrategyUtils.postRunnableDelay(new StartStrategyVirtualDisplayRunnable(context, intent), 1000L);
                return true;
            }
        } catch (Throwable th) {
            Log.iv(Log.TAG, "error : " + th);
        }
        return false;
    }

    @Override
    public String getName() {
        return "virtual";
    }

    public class StartStrategyVirtualDisplayRunnable implements Runnable {
        private Context mContext;
        private Intent mIntent;

        public StartStrategyVirtualDisplayRunnable(Context context, Intent intent) {
            mContext = context;
            mIntent = intent;
        }

        @Override
        public void run() {
            try {
                int flags;
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
                } else {
                    flags = PendingIntent.FLAG_UPDATE_CURRENT;
                }
                PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), mIntent, flags).send();
            } catch (Exception e) {
                Log.iv(Log.TAG, "error : " + e);
                try {
                    mContext.startActivity(mIntent);
                } catch (Exception exception) {
                    Log.iv(Log.TAG, "error : " + exception);
                }
            }
        }
    }

    private Object createDisplay(Context context) {
        try {
            @SuppressLint("WrongConstant")
            Object object = context.getSystemService(Context.DISPLAY_SERVICE);
            Method method = Class.forName("android.hardware.display.DisplayManager").getMethod("createVirtualDisplay", String.class, int.class, int.class, int.class, Surface.class, int.class);
            Object displayObject = method.invoke(object, "virtual_display_other", 500, 500, context.getResources().getConfiguration().densityDpi, null, 0);
            Method getDisplayMethod = Class.forName("android.hardware.display.VirtualDisplay").getMethod("getDisplay");
            return getDisplayMethod.invoke(displayObject);
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
        }
        return null;
    }

    public class VMPresentation extends Presentation {
        public VMPresentation(Context context, Display display) {
            super(context, display);
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            TextView textView = new TextView(getContext());
            textView.setBackgroundColor(View.MEASURED_STATE_MASK);
            textView.setTextSize(30.0f);
            textView.setText("MEASURED_STATE_MASK");
            textView.setTextColor(View.MEASURED_STATE_MASK);
        }
    }
}
