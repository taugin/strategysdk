package com.sogou.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bigcow.KeepAlive;
import com.bigcow.model.RemindParams;
import com.sogou.daemon.demo.R;
import com.sogou.log.Log;


public class MainActivity extends Activity {

    private RemindParams mRemindParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        Log.v(Log.TAG, "bundle : " + (bundle != null ? bundle.getString("test_bundle_key") : ""));
        mRemindParams = new RemindParams.Builder()
                .setLayoutType(RemindParams.LAYOUT_TYPE_1)
                .setTitleString("梦幻怪兽")
                .setDescString("梦幻怪兽是一款让人欲罢不能的RPG手游，主打四对四特殊回合制战斗，最多16只怪兽组成一个队伍。独创对战系统和上百种技能让每一次战斗更加热血和多元。终极进化你的怪兽，释放它们的真正力量，主宰PvP赛场！您准备好进入梦幻怪兽的世界了吗？\n")
                .setIconId(R.drawable.notify_icon_small)
                .setImageId(R.drawable.bc_image)
                .setActionString("下载试玩").build();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.show_app_detail) {
            startActivity(getSettingsDetail());
        } else if (v.getId() == R.id.show_remind_notification) {
            KeepAlive.showRemind(this, mRemindParams, KeepAlive.RemindMode.NOTIFICATION);
        } else if (v.getId() == R.id.show_remind_activity) {
            KeepAlive.showRemind(this, mRemindParams);
        } else if (v.getId() == R.id.show_remind_activity_notification) {
            KeepAlive.showRemind(this, mRemindParams, KeepAlive.RemindMode.ACTIVITY_AND_NOTIFICATION);
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
