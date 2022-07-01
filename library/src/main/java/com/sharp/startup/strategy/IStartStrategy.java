package com.sharp.startup.strategy;

import android.content.Context;
import android.content.Intent;

public interface IStartStrategy {
    int getNextStartDelay();
    boolean startActivityInBackground(Context context, Intent intent, boolean z);
    String getName();
}
