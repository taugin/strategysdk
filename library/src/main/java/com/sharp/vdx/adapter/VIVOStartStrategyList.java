package com.sharp.vdx.adapter;

import android.content.Context;

import com.sharp.vdx.strategy.StartStrategyAlarmImpl;
import com.sharp.vdx.strategy.StartStrategyBringForegroundImpl;
import com.sharp.vdx.strategy.StartStrategyFullScreenIntentImpl;
import com.sharp.vdx.strategy.StartStrategyJobServiceImpl;
import com.sharp.vdx.strategy.StartStrategyResetIntentImpl4VIVO;
import com.sharp.vdx.strategy.StartStrategySafeImpl;
import com.sharp.vdx.strategy.StartStrategyVirtualDisplayImpl;

public class VIVOStartStrategyList extends StartStrategyList {
    public static final String Strategy_Name = "vv_list";

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
        }
    }

    @Override
    public String getName() {
        return Strategy_Name;
    }
}
