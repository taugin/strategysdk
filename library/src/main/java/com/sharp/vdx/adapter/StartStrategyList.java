package com.sharp.vdx.adapter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.sharp.vdx.log.Log;
import com.sharp.vdx.strategy.IStartStrategy;
import com.sharp.vdx.strategy.StartStrategyAlarmImpl;
import com.sharp.vdx.strategy.StartStrategyBringForegroundImpl;
import com.sharp.vdx.strategy.StartStrategyFullScreenIntentImpl;
import com.sharp.vdx.strategy.StartStrategyJobServiceImpl;
import com.sharp.vdx.strategy.StartStrategyResetIntentImpl4MIUI;
import com.sharp.vdx.strategy.StartStrategyResetIntentImpl4VIVO;
import com.sharp.vdx.strategy.StartStrategySafeImpl;
import com.sharp.vdx.strategy.StartStrategyVirtualDisplayImpl;
import com.sharp.vdx.utils.BackActUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StartStrategyList extends BroadcastReceiver implements IStartStrategy {

    public static final String PREF_DEST_INTENT = "pref_dest_intent";

    public static final String TAG = "start_list";

    public static final String ACTION_START_COMPLETE = "list_action";

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
            Log.vf(Log.TAG, "on Receive(%s) %s= ", this.strategyName, this.startCompleteAction);
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
            Log.vf(Log.TAG, "name = %s,action = %s,isReceived = %s,delayTime = %s,className = %s", next.getName(), this.startCompleteAction, Boolean.valueOf(this.isStartComplete), nextStartDelay, component != null ? component.getClassName() : "");
            if (this.isStartComplete) {
                Log.vf(Log.TAG, "name = %s,abort", next.getName());
                unregisterBroadcast();
                return false;
            }
            next.startActivityInBackground(context, intent, z);
            this.strategyName = next.getName();
            BackActUtils.postRunnableDelay(new StartInBackgroundRunnable(this, context, intent, z), nextStartDelay);
        } else {
            unregisterBroadcast();
        }
        return true;

    }

    @Override
    public String getName() {
        return TAG;
    }

    public StartStrategyList(Context context) {
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
        this.startStrategyList.add(new StartStrategyVirtualDisplayImpl());
        this.startStrategyList.add(new StartStrategyAlarmImpl());
        this.startStrategyList.add(new StartStrategySafeImpl());
        this.startStrategyList.add(new StartStrategyFullScreenIntentImpl());
        this.startStrategyList.add(new StartStrategyBringForegroundImpl());
        this.startStrategyList.add(new StartStrategyJobServiceImpl());
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
        Log.vf(Log.TAG, "start action = %s", this.startCompleteAction);
        return startActivityInBackgroundLocked(context, intent, z);
    }


    public class StartInBackgroundRunnable implements Runnable {

        public final Context mContext;

        public final Intent mIntent;

        public final boolean isEnable;

        public final StartStrategyList startStrategyList;

        public StartInBackgroundRunnable(StartStrategyList startStrategyList, Context context, Intent intent, boolean z) {
            this.startStrategyList = startStrategyList;
            this.mContext = context;
            this.mIntent = intent;
            this.isEnable = z;
        }

        public void run() {
            if (startStrategyList != null) {
                this.startStrategyList.startActivityInBackground(this.mContext, this.mIntent, this.isEnable);
            }
        }
    }
}
