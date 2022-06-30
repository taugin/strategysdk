package com.bigcow.component;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;

import androidx.core.content.ContextCompat;

import com.bigcow.future.R;

public class ResidentService extends DaemonBaseService {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundForService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundForService();
        startStayService(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundForService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            Notification.Builder builder = new Notification.Builder(this, getPackageName());
            builder.setSmallIcon(R.drawable.bc_icon_tran);
            Notification notification = builder.build();
            try {
                startForeground(getPackageName().hashCode(), notification);
            } catch (Exception e) {
            }
        }
    }

    public static void startStayService(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    ContextCompat.startForegroundService(context, new Intent(context, BigcowFutureService.class));
                    return false;
                }
            });
        } else {
            ContextCompat.startForegroundService(context, new Intent(context, BigcowFutureService.class));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        try {
            NotificationChannel channel = new NotificationChannel(getPackageName(), getChannelName(), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            channel.setDescription("android");
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            nm.createNotificationChannel(channel);
        } catch (Exception e) {
        }
    }

    private String getChannelName() {
        return "Default Channel";
    }
}
