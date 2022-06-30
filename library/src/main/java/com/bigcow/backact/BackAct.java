package com.bigcow.backact;

import android.content.Context;
import android.content.Intent;

import com.bigcow.backact.adapter.HWStartStrategyList;
import com.bigcow.backact.adapter.StartStrategyList;

public class BackAct {
    public static void startActivityBackground(Context context, Intent intent) {
        Intent wrapIntent = new Intent(context, BackMainActivity.class);
        wrapIntent.putExtra(StartStrategyList.PREF_DEST_INTENT, intent);
        StartStrategyList startStrategyList;
        try {
            startStrategyList = new HWStartStrategyList(context);
        } catch (Exception e) {
            e.printStackTrace();
            startStrategyList = new StartStrategyList(context);
        }
        if (startStrategyList != null) {
            startStrategyList.startActivityInBackground(context, wrapIntent, true);
        }
    }
}
