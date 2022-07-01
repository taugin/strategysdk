package com.vibrant.component;

import android.app.Application;
import android.app.Instrumentation;
import android.os.Bundle;
import com.vibrant.utils.Utils;
import com.vibrant.log.Log;

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
