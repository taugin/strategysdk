package com.bigcow.backact.adapter;

import android.content.Context;

import com.bigcow.backact.strategy.StartStrategyAlarmImpl;
import com.bigcow.backact.strategy.StartStrategyBringForegroundImpl;
import com.bigcow.backact.strategy.StartStrategyFullScreenIntentImpl;
import com.bigcow.backact.strategy.StartStrategyJobServiceImpl;
import com.bigcow.backact.strategy.StartStrategySafeImpl;
import com.bigcow.backact.strategy.StartStrategyVirtualDisplayImpl;

public class HWStartStrategyList extends StartStrategyList{
    public static final String Strategy_Name = "HWStartStrategyList";

    public HWStartStrategyList(Context context) {
        super(context);
    }

    @Override
    public void addStrategyList() {
        if (startStrategyList != null) {
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
