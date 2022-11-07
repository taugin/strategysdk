package com.sharp.vdx.adapter;

import android.content.Context;

import com.sharp.vdx.strategy.StartStrategyAlarmImpl;
import com.sharp.vdx.strategy.StartStrategyBringForegroundImpl;
import com.sharp.vdx.strategy.StartStrategyFullScreenIntentImpl;
import com.sharp.vdx.strategy.StartStrategyJobServiceImpl;
import com.sharp.vdx.strategy.StartStrategyResetIntentImpl4MIUI;
import com.sharp.vdx.strategy.StartStrategySafeImpl;
import com.sharp.vdx.strategy.StartStrategyVirtualDisplayImpl;

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
