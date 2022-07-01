package com.sharp.startup.adapter;

import android.content.Context;

import com.sharp.startup.strategy.StartStrategyAlarmImpl;
import com.sharp.startup.strategy.StartStrategyBringForegroundImpl;
import com.sharp.startup.strategy.StartStrategyFullScreenIntentImpl;
import com.sharp.startup.strategy.StartStrategyJobServiceImpl;
import com.sharp.startup.strategy.StartStrategyResetIntentImpl4VIVO;
import com.sharp.startup.strategy.StartStrategySafeImpl;
import com.sharp.startup.strategy.StartStrategyVirtualDisplayImpl;

public class VIVOStartStrategyList extends StartStrategyList {
    public static final String Strategy_Name = "VIVOStartStrategyList";

    public VIVOStartStrategyList(Context context) {
        super(context);
    }

    @Override
    public void addStrategyList() {
        if (startStrategyList != null) {
            this.startStrategyList.add(new StartStrategyResetIntentImpl4VIVO());
            this.startStrategyList.add(new StartStrategyVirtualDisplayImpl());
            this.startStrategyList.add(new StartStrategySafeImpl());
            this.startStrategyList.add(new StartStrategyAlarmImpl());
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
