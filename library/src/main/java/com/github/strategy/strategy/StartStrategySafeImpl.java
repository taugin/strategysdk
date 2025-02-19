package com.github.strategy.strategy;

import android.content.Context;
import android.content.Intent;

import com.github.strategy.utils.StrategyUtils;


public class StartStrategySafeImpl implements IStartStrategy{
    @Override
    public int getNextStartDelay() {
        return 4000;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        return StrategyUtils.startActivityOrUsePending(context, intent);
    }

    @Override
    public String getName() {
        return "safe";
    }
}
