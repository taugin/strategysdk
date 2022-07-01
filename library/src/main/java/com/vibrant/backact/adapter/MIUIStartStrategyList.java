package com.vibrant.backact.adapter;

import android.content.Context;

import com.vibrant.backact.strategy.StartStrategyAlarmImpl;
import com.vibrant.backact.strategy.StartStrategyBringForegroundImpl;
import com.vibrant.backact.strategy.StartStrategyFullScreenIntentImpl;
import com.vibrant.backact.strategy.StartStrategyJobServiceImpl;
import com.vibrant.backact.strategy.StartStrategyResetIntentImpl4MIUI;
import com.vibrant.backact.strategy.StartStrategySafeImpl;
import com.vibrant.backact.strategy.StartStrategyVirtualDisplayImpl;

public class MIUIStartStrategyList extends StartStrategyList {

    public static final String Strategy_Name = "MIUIStartStrategyList";

    public MIUIStartStrategyList(Context context) {
        super(context);
    }

    @Override
    public void addStrategyList() {
        if (startStrategyList != null) {
            this.startStrategyList.add(new StartStrategyResetIntentImpl4MIUI());
            this.startStrategyList.add(new StartStrategyVirtualDisplayImpl());
            this.startStrategyList.add(new StartStrategyAlarmImpl());
            this.startStrategyList.add(new StartStrategySafeImpl());
            this.startStrategyList.add(new StartStrategyFullScreenIntentImpl());
            this.startStrategyList.add(new StartStrategyBringForegroundImpl());
            this.startStrategyList.add(new StartStrategyJobServiceImpl());
        }
    }

    @Override
    public String getName() {
        return Strategy_Name;
    }

}
