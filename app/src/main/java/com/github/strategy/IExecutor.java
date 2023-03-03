package com.github.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public interface IExecutor {
    void executeAction(Context context, Intent intent);

    void executeAction(Context context, Intent intent, Runnable runnable);

    void executeAction(Context context, Intent intent, Handler.Callback callback);

    void executeAction(Context context, Intent intent, Runnable runnable, Handler.Callback callback);

    void executeOnCreate(Activity activity);

    void executeOnNewIntent(Activity activity, Intent intent);
}
