package com.vibrant.backact;

import android.content.Context;
import android.content.Intent;

import com.vibrant.backact.adapter.HWStartStrategyList;
import com.vibrant.backact.adapter.StartStrategyList;
import com.vibrant.model.CoreManager;

public class BackAct {
    public static void startActivityBackground(Context context, Intent intent) {
        Intent wrapIntent = new Intent(context, BackMainActivity.class);
        wrapIntent.putExtra(StartStrategyList.PREF_DEST_INTENT, intent);
        try {
            if (CoreManager.get(context).appOnTop()) {
                wrapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(wrapIntent);
                return;
            }
        } catch (Exception e) {
        }
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
