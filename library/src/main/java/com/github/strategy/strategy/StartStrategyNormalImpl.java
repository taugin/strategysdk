package com.github.strategy.strategy;

import android.content.Context;
import android.content.Intent;

import com.github.strategy.utils.StrategyUtils;


public class StartStrategyNormalImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        if (!StrategyUtils.isInForeground(context)) {
            return false;
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public String getName() {
        return "normal";
    }
}
