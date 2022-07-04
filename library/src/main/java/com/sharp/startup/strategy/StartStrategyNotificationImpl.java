package com.sharp.startup.strategy;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.sharp.SharpRemind;
import com.sharp.component.RemindFutureService;
import com.sharp.model.CoreManager;

public class StartStrategyNotificationImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        Intent serviceIntent = new Intent(context, RemindFutureService.class);
        serviceIntent.putExtra(CoreManager.EXTRA_SHOW_REMIND, true);
        serviceIntent.putExtra(CoreManager.EXTRA_REMIND_MODE, SharpRemind.RemindMode.NOTIFICATION);
        ContextCompat.startForegroundService(context, serviceIntent);
        return true;
    }

    @Override
    public String getName() {
        return "notification";
    }
}
