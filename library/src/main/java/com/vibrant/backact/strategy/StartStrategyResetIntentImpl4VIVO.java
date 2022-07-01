package com.vibrant.backact.strategy;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Field;

public class StartStrategyResetIntentImpl4VIVO implements IStartStrategy {
    public static final String f18465a = "ResetIntentV";

    @Override
    public int getNextStartDelay() {
        return 0;
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        try {
            Field declaredField = intent.getClass().getDeclaredField("mIsVivoWidget");
            //AppLogger.m22454c(f18465a, "field = " + declaredField, new Object[0]);
            declaredField.setAccessible(true);
            Object obj = declaredField.get(intent);
            //AppLogger.m22454c(f18465a, "value = " + obj, new Object[0]);
            declaredField.set(intent, Boolean.TRUE);
            //AppLogger.m22454c(f18465a, "set success", new Object[0]);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            //AppLogger.m22454c(f18465a, "field exp = " + Log.getStackTraceString(e), new Object[0]);
        }
        return true;
    }

    @Override
    public String getName() {
        return "ResetIntentV";
    }
}
