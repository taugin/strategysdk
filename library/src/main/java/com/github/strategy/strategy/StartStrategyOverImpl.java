package com.github.strategy.strategy;

import android.content.Context;
import android.content.Intent;

import com.github.strategy.utils.BackActUtils;

public class StartStrategyOverImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        if (BackActUtils.sOverRunnable != null) {
            BackActUtils.sOverRunnable.run();
        }
        return true;
    }

    @Override
    public String getName() {
        return "over";
    }
}
