package com.vibrant.backact.strategy;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

public class StartStrategyResetIntentImpl4MIUI implements IStartStrategy {
    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        try {
            Method[] declaredMethods = Intent.class.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.getName().equals("addMiuiFlags")) {
                    method.setAccessible(true);
                    try {
                        method.invoke(intent, 2);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        return true;
    }

    @Override
    public String getName() {
        return "ResetIntentM";
    }
}
