package com.sharp.demo;

import android.app.PendingIntent;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.ImageFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VirtualDisplayUtils {
    private static final String TAG = "virtual";

    public static void start(Context context, Intent intent) {
        try {
            Bitmap bitmap = null;
            ImageReader imageReader = ImageReader.newInstance(500, 500, ImageFormat.JPEG, 1);
            DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            VirtualDisplay virtualDisplay = displayManager.createVirtualDisplay("virtual_display_other", 500, 500, context.getResources().getConfiguration().densityDpi, null, 0);
            Display display = virtualDisplay.getDisplay();
            new VMPresentation(context, display).show();
            new Handler(Looper.getMainLooper()).postDelayed(new StartStrategyVirtualDisplayRunnable(context, intent), 1000L);
        } catch (Throwable th) {
            Log.iv(TAG, "error : " + th);
        }
    }

    public static class StartStrategyVirtualDisplayRunnable implements Runnable {
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
                Log.iv(TAG, "error : " + e);
                try {
                    mContext.startActivity(mIntent);
                } catch (Exception exception) {
                    Log.iv(TAG, "error : " + exception);
                }
            }
        }
    }

    public static class VMPresentation extends Presentation {
        public VMPresentation(Context context, Display display) {
            super(context, display);
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            TextView textView = new TextView(getContext());
            setContentView(textView);
            textView.setBackgroundColor(View.MEASURED_STATE_MASK);
            textView.setTextSize(30.0f);
            textView.setText("MEASURED_STATE_MASK");
            textView.setTextColor(View.MEASURED_STATE_MASK);
        }
    }
}
