package com.github.strategy.adapter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.github.strategy.log.Log;
import com.github.strategy.strategy.IStartStrategy;
import com.github.strategy.strategy.StartStrategyAlarmImpl;
import com.github.strategy.strategy.StartStrategyBringForegroundImpl;
import com.github.strategy.strategy.StartStrategyFullScreenIntentImpl;
import com.github.strategy.strategy.StartStrategyOverImpl;
import com.github.strategy.strategy.StartStrategyResetIntentImpl4MIUI;
import com.github.strategy.strategy.StartStrategyResetIntentImpl4VIVO;
import com.github.strategy.strategy.StartStrategySafeImpl;
import com.github.strategy.strategy.StartStrategyVDImpl;
import com.github.strategy.utils.Stat;
import com.github.strategy.utils.StrategyUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BaseStrategyList extends BroadcastReceiver implements IStartStrategy {

    public static final String TAG = "StartStrategyList";

    public static final String ACTION_START_COMPLETE = "android.intent.action.COMPLETE_STRATEGY";

    public List<IStartStrategy> startStrategyList = new ArrayList();

    private Iterator<IStartStrategy> startStrategyIterator;

    private String startCompleteAction;

    private boolean isStartComplete = false;

    private Context mContext;

    private String strategyName = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(this.startCompleteAction)) {
            this.isStartComplete = true;
            Log.vf(Log.TAG, "on Receive(%s) %s ", this.strategyName, this.startCompleteAction);
            Stat.reportEvent(context, "strategy_start_bridge_name", strategyName);
        }
    }

    @Override
    public int getNextStartDelay() {
        return 0;
    }

    public boolean startActivityInBackgroundLocked(Context context, Intent intent, boolean z) {
        if (this.startStrategyIterator.hasNext()) {
            IStartStrategy next = this.startStrategyIterator.next();
            intent.putExtra(ACTION_START_COMPLETE, this.startCompleteAction);
            long nextStartDelay = next.getNextStartDelay();
            ComponentName component = intent.getComponent();
            Log.vf(Log.TAG, "strategy name = %s,action = %s,isReceived = %s,delayTime = %s,className = %s", next.getName(), this.startCompleteAction, Boolean.valueOf(this.isStartComplete), nextStartDelay, component != null ? component.getClassName() : "");
            if (this.isStartComplete) {
                Log.vf(Log.TAG, "strategy name = %s,abort", next.getName());
                unregisterBroadcast();
                return false;
            }
            next.startActivityInBackground(context, intent, z);
            this.strategyName = next.getName();
            StrategyUtils.postRunnableDelay(new StartInBackgroundRunnable(this, context, intent, z), nextStartDelay);
        } else {
            unregisterBroadcast();
        }
        return true;
    }

    @Override
    public String getName() {
        return TAG;
    }

    public BaseStrategyList(Context context) {
        addStrategyList();
        this.startCompleteAction = UUID.randomUUID().toString().replace("-", "");
        this.mContext = context.getApplicationContext();
        registerBroadcast();
        this.startStrategyIterator = this.startStrategyList.iterator();
    }

    /* renamed from: a */
    public static void sendStartCompleteBroadcast(Context context, String str) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("send = ");
            sb.append(str);
            Log.vf(Log.TAG, sb.toString(), new Object[0]);
            Intent intent = new Intent(str);
            intent.setPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES/*32*/);
            context.sendBroadcast(intent);
            Log.vf(Log.TAG, "send completed = " + str, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStrategyList() {
        this.startStrategyList.add(new StartStrategyResetIntentImpl4MIUI());
        this.startStrategyList.add(new StartStrategyResetIntentImpl4VIVO());
        this.startStrategyList.add(new StartStrategyVDImpl());
        this.startStrategyList.add(new StartStrategyAlarmImpl());
        this.startStrategyList.add(new StartStrategySafeImpl());
        this.startStrategyList.add(new StartStrategyFullScreenIntentImpl());
        this.startStrategyList.add(new StartStrategyBringForegroundImpl());
        this.startStrategyList.add(new StartStrategyOverImpl());
    }

    private void registerBroadcast() {
        if (!TextUtils.isEmpty(this.startCompleteAction)) {
            try {
                this.mContext.registerReceiver(this, new IntentFilter(this.startCompleteAction));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unregisterBroadcast() {
        if (!TextUtils.isEmpty(this.startCompleteAction)) {
            try {
                this.mContext.unregisterReceiver(this);
                this.startCompleteAction = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean startActivityInBackground(Context context, Intent intent, boolean z) {
        Log.vf(Log.TAG, "------start action = %s", this.startCompleteAction);
        return startActivityInBackgroundLocked(context, intent, z);
    }


    public class StartInBackgroundRunnable implements Runnable {

        public final Context mContext;

        public final Intent mIntent;

        public final boolean isEnable;

        public final BaseStrategyList baseStrategyList;

        public StartInBackgroundRunnable(BaseStrategyList baseStrategyList, Context context, Intent intent, boolean z) {
            this.baseStrategyList = baseStrategyList;
            this.mContext = context;
            this.mIntent = intent;
            this.isEnable = z;
        }

        public void run() {
            if (baseStrategyList != null) {
                this.baseStrategyList.startActivityInBackground(this.mContext, this.mIntent, this.isEnable);
            }
        }
    }
}
