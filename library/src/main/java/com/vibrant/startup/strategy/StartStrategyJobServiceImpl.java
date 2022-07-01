package com.vibrant.startup.strategy;

import android.content.Context;
import android.content.Intent;

public class StartStrategyJobServiceImpl implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
//        if (ROMUtils.m22559s() && Build.VERSION.SDK_INT >= 26) {
//            BackActUtils.moveToFront(context);
//            CGRunAJobService.m21254a(context, intent, true);
//        }
//        return CGRunAJobService.m21254a(context, intent, z);
        return false;
    }

    @Override
    public String getName() {
        return "jobService";
    }
}
