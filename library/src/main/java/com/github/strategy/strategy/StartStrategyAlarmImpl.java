package com.github.strategy.strategy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class StartStrategyAlarmImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 3000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        PendingIntent activity = PendingIntent.getActivity(context, 10102, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return false;
        }
        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((long) 1000), activity);
        } catch (Exception | Error e) {
        }
        return true;
    }

    @Override
    public String getName() {
        return "Alarm";
    }
}
