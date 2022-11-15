package com.sharp.vdx;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sharp.vdx.adapter.HWStartStrategyList;
import com.sharp.vdx.adapter.StartStrategyList;
import com.sharp.vdx.log.Log;

public class VdxAction {
    public static void execute(Context context, Intent intent) {
        StartStrategyList startStrategyList;
        try {
            startStrategyList = new HWStartStrategyList(context);
        } catch (Exception e) {
            Log.v(Log.TAG, "error : " + e);
            startStrategyList = new StartStrategyList(context);
        }
        if (startStrategyList != null) {
            startStrategyList.startActivityInBackground(context, intent, true);
        }
    }

    public static void sendResult(Context context, String action) {
        if (!TextUtils.isEmpty(action)) {
            StartStrategyList.sendStartCompleteBroadcast(context, action);
        }
    }
}
