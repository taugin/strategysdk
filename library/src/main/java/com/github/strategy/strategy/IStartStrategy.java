package com.github.strategy.strategy;

import android.content.Context;
import android.content.Intent;

public interface IStartStrategy {
    String EXTRA_REMOVE_VIEWS = "extra_remove_views";
    String EXTRA_CONTENT_PENDING_INTENT = "extra_content_pending_intent";
    String EXTRA_FULLSCREEN_PENDING_INTENT = "extra_fullscreen_pending_intent";
    String EXTRA_NOTIFICATION_ID = "extra_notification_id";
    String EXTRA_CANCEL_ALL = "extra_cancel_all";
    int getNextStartDelay();
    boolean startActivityInBackground(Context context, Intent intent, boolean z);
    String getName();
}
