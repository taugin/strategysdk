package com.sharp.daemon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import com.sharp.component.DaemonMain;
import com.sharp.env.DaemonEnv;
import com.sharp.log.Log;
import com.sharp.utils.Utils;

import java.util.ArrayList;

public class JavaDaemon {
    private static final String COLON_SEPARATOR = ":";
    private static JavaDaemon a = new JavaDaemon();
    private DaemonEnv daemonEnv;

    public static JavaDaemon getInstance() {
        return a;
    }

    public DaemonEnv getDaemonEnv() {
        return this.daemonEnv;
    }

    public void init(Context context, Intent intent, Intent intent2, Intent intent3) {
        this.daemonEnv = new DaemonEnv();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        this.daemonEnv.publicDir = applicationInfo.publicSourceDir;
        this.daemonEnv.nativeDir = applicationInfo.nativeLibraryDir;
        this.daemonEnv.serviceIntent = intent;
        this.daemonEnv.receiverIntent = intent2;
        this.daemonEnv.instrumentIntent = intent3;
        this.daemonEnv.cmdLine = Utils.getCmdLine();
    }

    public void startAppLock(Context context, String[] strArr) {
        boolean specProcessHasStarted;
        String cmdLine = Utils.getCmdLine();
        Log.v(Log.TAG, "cmdLine : " + cmdLine);
        if (cmdLine != null && cmdLine.startsWith(context.getPackageName()) && cmdLine.contains(COLON_SEPARATOR)) {
            String substring = cmdLine.substring(cmdLine.lastIndexOf(COLON_SEPARATOR) + 1);
            ArrayList arrayList = new ArrayList();
            if (strArr != null) {
                specProcessHasStarted = false;
                for (String str : strArr) {
                    if (str.equals(substring)) {
                        specProcessHasStarted = true;
                    } else {
                        arrayList.add(str);
                    }
                }
            } else {
                specProcessHasStarted = false;
            }
            if (specProcessHasStarted) {
                Log.v(Log.TAG, "app lock file start : [" + substring + "]");
                DaemonMain.lockFile(context.getFilesDir() + "/" + substring + "_daemon");
                Log.v(Log.TAG, "app lock file finish : [" + substring + "]");
                String[] strArr2 = new String[arrayList.size()];
                for (int i = 0; i < strArr2.length; i++) {
                    strArr2[i] = context.getFilesDir() + "/" + ((String) arrayList.get(i)) + "_daemon";
                }
                new AppProcessThread(context, strArr2, substring).start();
            }
        }
    }
}
