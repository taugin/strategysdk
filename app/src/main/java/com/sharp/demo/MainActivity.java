package com.sharp.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.sharp.SharpRemind;
import com.sharp.daemon.demo.R;
import com.sharp.model.OnGoingParams;
import com.sharp.model.RemindParams;

import java.util.Random;


public class MainActivity extends Activity {

    private RemindParams.Builder mRemindParamsBuilder;
    private static final String[] TITLE_ARRAY = {
            "梦幻怪兽",
            "Papa's Pizzeria To Go!",
            "钢铁战队 (Iron Marines)",
            "Baseball Clash：实时游戏"
    };
    private static final String[] DESC_ARRAY = {
            "梦幻怪兽是一款让人欲罢不能的RPG手游，主打四对四特殊回合制战斗，最多16只怪兽组成一个队伍。独创对战系统和上百种技能让每一次战斗更加热血和多元。终极进化你的怪兽，释放它们的真正力量，主宰PvP赛场！您准备好进入梦幻怪兽的世界了吗？",
            "Top, bake, and serve pizzas in Papa's Pizzeria To Go! This all-new version of the classic restaurant sim features updated gameplay and brand-new controls reimagined for smaller screens.",
            "这是游戏创作者继获奖作品Kingdom Rush(《王国保卫战》)三部曲之后的又一超凡太空奇幻历险之作。",
            "老少咸宜的多人棒球游戏！迎战对手，享受酣畅淋漓的精彩比赛！"
    };
    private static final int[] ICON_ARRAY = {
            R.drawable.app_icon_1,
            R.drawable.app_icon_2,
            R.drawable.app_icon_3,
            R.drawable.app_icon_4
    };
    private static final int[] IMAGE_ARRAY = {
            R.drawable.app_image_1,
            R.drawable.app_image_2,
            R.drawable.app_image_3,
            R.drawable.app_image_4
    };

    private static int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        Log.v(Log.TAG, "start args : " + App.bundleToString(bundle));
    }

    private void generateRemindParams() {
        mIndex = new Random(System.currentTimeMillis()).nextInt(ICON_ARRAY.length);
        mRemindParamsBuilder = new RemindParams.Builder()
                .setLayoutType(RemindParams.LAYOUT_REMIND_1)
                .setNotificationId(new Random().nextInt(10000))
                .setTitleString(TITLE_ARRAY[mIndex])
                .setDescString(DESC_ARRAY[mIndex])
                .setIconId(ICON_ARRAY[mIndex])
                .setImageId(IMAGE_ARRAY[mIndex])
                .setActionString("下载试玩");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.show_app_detail) {
            startActivity(getSettingsDetail());
        } else if (v.getId() == R.id.show_remind_notification) {
            generateRemindParams();
            Bundle bundle = new Bundle();
            bundle.putString("start_app_from", String.valueOf(SharpRemind.RemindMode.NOTIFICATION));
            bundle.putString("remind_title", TITLE_ARRAY[mIndex]);
            bundle.putString("remind_desc", DESC_ARRAY[mIndex]);
            mRemindParamsBuilder.setBundle(bundle);
            SharpRemind.showRemind(this, mRemindParamsBuilder.build(), SharpRemind.RemindMode.NOTIFICATION);
        } else if (v.getId() == R.id.show_remind_activity) {
            generateRemindParams();
            Bundle bundle = new Bundle();
            bundle.putString("start_app_from", String.valueOf(SharpRemind.RemindMode.ACTIVITY));
            bundle.putString("remind_title", TITLE_ARRAY[mIndex]);
            bundle.putString("remind_desc", DESC_ARRAY[mIndex]);
            mRemindParamsBuilder.setBundle(bundle);
            SharpRemind.showRemind(this, mRemindParamsBuilder.build());
        } else if (v.getId() == R.id.show_remind_activity_notification) {
            generateRemindParams();
            Bundle bundle = new Bundle();
            bundle.putString("start_app_from", String.valueOf(SharpRemind.RemindMode.ACTIVITY_AND_NOTIFICATION));
            bundle.putString("remind_title", TITLE_ARRAY[mIndex]);
            bundle.putString("remind_desc", DESC_ARRAY[mIndex]);
            mRemindParamsBuilder.setBundle(bundle);
            SharpRemind.showRemind(this, mRemindParamsBuilder.build(), SharpRemind.RemindMode.ACTIVITY_AND_NOTIFICATION);
        } else if (v.getId() == R.id.update_ongoing) {
            OnGoingParams.Builder builder = new OnGoingParams.Builder();
            builder.setLayoutType(OnGoingParams.LAYOUT_ONGOING_1);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setIconId(R.drawable.ic_launcher);
            builder.setTitleString("战RPG手游");
            builder.setDescString("收服、训练和养成超过2000只怪兽，打造专属于你的队伍通往胜利之路！超大型的怪物对战RPG手游！");
            builder.setActionString("下载试玩");
            Bundle extra = new Bundle();
            extra.putString("start_app_from", String.valueOf(SharpRemind.RemindMode.ONGOING));
            extra.putString("args", "update ongoing");
            builder.setBundle(extra);
            SharpRemind.updateOnGoing(this, builder.build());
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
