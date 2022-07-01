package com.vibrant.backact.strategy;

import android.app.PendingIntent;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.vibrant.backact.utils.BackActUtils;


public class StartStrategyVirtualDisplayImpl implements IStartStrategy {
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
            new MockPresentation(context, ((DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE)).createVirtualDisplay("virtual_display_other", 500, 500, context.getResources().getConfiguration().densityDpi, null, 0).getDisplay()).show();
            BackActUtils.postRunnableDelay(new StartStrategyVirtualDisplayRunnable(this, context, intent), 1000L);
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return false;
    }

    @Override
    public String getName() {
        return "virtual";
    }

    public class StartStrategyVirtualDisplayRunnable implements Runnable {
        private StartStrategyVirtualDisplayImpl mStartStrategyVirtualDisplayImpl;
        private Context mContext;
        private Intent mIntent;

        public StartStrategyVirtualDisplayRunnable(StartStrategyVirtualDisplayImpl impl, Context context, Intent intent) {
            mStartStrategyVirtualDisplayImpl = impl;
            mContext  = context;
            mIntent = intent;
        }

        @Override
        public void run() {
            try {
                PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT).send();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class MockPresentation extends Presentation {
        public MockPresentation(Context context, Display display) {
            super(context, display);
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            TextView textView = new TextView(getContext());
            textView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            textView.setTextSize(30.0f);
            textView.setText("hahahahahahahaha");
            textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        }
    }
}
