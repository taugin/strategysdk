package com.sharp.vdx;

import android.content.Context;
import android.content.Intent;

import com.sharp.vdx.adapter.HWStartStrategyList;
import com.sharp.vdx.adapter.StartStrategyList;

public class BackAct {
    public static void startActivityBackground(Context context, Intent intent) {
        StartStrategyList startStrategyList;
        try {
            startStrategyList = new HWStartStrategyList(context);
        } catch (Exception e) {
            e.printStackTrace();
            startStrategyList = new StartStrategyList(context);
        }
        if (startStrategyList != null) {
            startStrategyList.startActivityInBackground(context, intent, true);
        }
    }
}
