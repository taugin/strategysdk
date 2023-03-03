package com.github.strategy;


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
}
