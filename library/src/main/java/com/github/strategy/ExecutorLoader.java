package com.github.strategy;


import com.github.strategy.log.Log;

public class ExecutorLoader {
    private static IExecutor sIExecutor;

    public static IExecutor getExecutor() {
        synchronized (IExecutor.class) {
            if (sIExecutor == null) {
                createInstance();
            }
        }
        return sIExecutor;
    }

    private static void createInstance() {
        synchronized (IExecutor.class) {
            if (sIExecutor == null) {
                sIExecutor = new ActionExecutor();
            }
        }
    }

    public static void init() {
        String className = StrategyActivity.class.getName();
        try {
            Class.forName(className).getMethod("setExecutor", IExecutor.class).invoke(null, getExecutor());
        } catch (Exception | Error e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }
}
