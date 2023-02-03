package com.github.strategy.adapter;

import android.content.Context;

import com.github.strategy.strategy.StartStrategyAlarmImpl;
import com.github.strategy.strategy.StartStrategyBringForegroundImpl;
import com.github.strategy.strategy.StartStrategyFullScreenIntentImpl;
import com.github.strategy.strategy.StartStrategyNotificationImpl;
import com.github.strategy.strategy.StartStrategyOverImpl;
import com.github.strategy.strategy.StartStrategyResetIntentImpl4VIVO;
import com.github.strategy.strategy.StartStrategySafeImpl;
import com.github.strategy.strategy.StartStrategyVDImpl;

public class VIVOBaseStrategyList extends BaseStrategyList {
    public static final String Strategy_Name = "VIVOStartStrategyList";

    public VIVOBaseStrategyList(Context context) {
        super(context);
    }

    @Override
    public void addStrategyList() {
        if (startStrategyList != null) {
            this.startStrategyList.add(new StartStrategyResetIntentImpl4VIVO());
            this.startStrategyList.add(new StartStrategyVDImpl());
            this.startStrategyList.add(new StartStrategySafeImpl());
            this.startStrategyList.add(new StartStrategyAlarmImpl());
            this.startStrategyList.add(new StartStrategyFullScreenIntentImpl());
            this.startStrategyList.add(new StartStrategyBringForegroundImpl());
            this.startStrategyList.add(new StartStrategyNotificationImpl());
            this.startStrategyList.add(new StartStrategyOverImpl());
        }
    }

    @Override
    public String getName() {
        return Strategy_Name;
    }
}
