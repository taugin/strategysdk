package com.sharp.vdx.strategy;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

public class StartStrategyAlarmImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 3000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        if (context == null) {
            return false;
        }
        int flags;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent activity = PendingIntent.getActivity(context, 10102, intent, flags);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 19 && context.checkCallingOrSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((long) 1000), activity);
            return true;
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((long) 1000), activity);
        return true;
    }

    @Override
    public String getName() {
        return "alarm";
    }
}
