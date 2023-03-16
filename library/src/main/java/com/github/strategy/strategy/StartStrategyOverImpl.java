package com.github.strategy.strategy;

import android.content.Context;
import android.content.Intent;

import com.github.strategy.utils.StrategyUtils;

public class StartStrategyOverImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        StrategyUtils.executeOverAction(context, intent);
        return true;
    }

    @Override
    public String getName() {
        return "over";
    }
}
