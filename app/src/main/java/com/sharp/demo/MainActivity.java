package com.sharp.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.strategy.IExecutor;
import com.sharp.daemon.demo.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.show_app_detail) {
            startActivity(getSettingsDetail());
        } else if (v.getId() == R.id.start_activity) {
            Intent intent = new Intent(this, ReminderActivity.class);
            VxUtils.executeRunnable(getApplicationContext(), new Runnable() {
                @Override
                public void run() {
                    IExecutor iExecutor = VxUtils.getExecutor();
                    if (iExecutor != null) {
                        iExecutor.executeAction(getApplicationContext(), intent, new Runnable() {
                            @Override
                            public void run() {
                            }
                        }, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(@NonNull Message msg) {
                                if (msg != null) {
                                    Log.v(Log.TAG, "msg.obj : " + msg.obj);
                                }
                                return false;
                            }
                        });
                    }
                }
            });
        } else if (v.getId() == R.id.load_dex) {
        }
    }

    private Intent getSettingsDetail() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
