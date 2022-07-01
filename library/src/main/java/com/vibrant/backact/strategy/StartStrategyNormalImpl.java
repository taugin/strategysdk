package com.vibrant.backact.strategy;

import android.content.Context;
import android.content.Intent;

import com.vibrant.backact.utils.BackActUtils;

public class StartStrategyNormalImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        if (!BackActUtils.isInForeground(context)) {
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
