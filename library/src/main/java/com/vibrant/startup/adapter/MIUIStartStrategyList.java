package com.vibrant.startup.adapter;

import android.content.Context;

import com.vibrant.startup.strategy.StartStrategyAlarmImpl;
import com.vibrant.startup.strategy.StartStrategyBringForegroundImpl;
import com.vibrant.startup.strategy.StartStrategyFullScreenIntentImpl;
import com.vibrant.startup.strategy.StartStrategyJobServiceImpl;
import com.vibrant.startup.strategy.StartStrategyResetIntentImpl4MIUI;
import com.vibrant.startup.strategy.StartStrategySafeImpl;
import com.vibrant.startup.strategy.StartStrategyVirtualDisplayImpl;

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
