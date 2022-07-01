package com.sharp.component;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import com.sharp.utils.Utils;
import com.sharp.log.Log;

public class DaemonInstrumentation extends Instrumentation {
    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
        Log.v(Log.TAG, "callApplicationOnCreate");
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.v(Log.TAG, "onCreate");
        Utils.startServiceOrBindService(getTargetContext(), DaemonService.class);
    }
}
