package com.vibrant.component;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibrant.future.R;
import com.vibrant.log.Log;
import com.vibrant.model.CoreManager;
import com.vibrant.model.RemindParams;

public class RemindActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "remind";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        cancelNotificationIfNeed();
        updateView();
        updateWindow();
    }

    private void updateView() {
        RemindParams params = CoreManager.get(this).getRemindParams();
        if (params == null) {
            Log.v(TAG, "OnGoingParams is null");
            finish();
            return;
        }
        int type = params.getNotificationLayout();
        if (type <= 0) {
            Log.v(TAG, "LayoutType is not set, use default layout");
            type = RemindParams.LAYOUT_REMIND_1;
        }
        String titleString = CoreManager.get(this).getTitleString(params);
        if (TextUtils.isEmpty(titleString)) {
            Log.v(TAG, "TitleString is not set");
            finish();
            return;
        }
        String descString = CoreManager.get(this).getDescString(params);
        if (TextUtils.isEmpty(descString)) {
            Log.v(TAG, "DescString is not set");
            finish();
            return;
        }
        String actionString = CoreManager.get(this).getActionString(params);
        if (TextUtils.isEmpty(actionString)) {
            Log.v(TAG, "ActionString is not set");
            finish();
            return;
        }
        Bitmap iconBitmap = CoreManager.get(this).getIconBitmap(params);
        if (iconBitmap == null) {
            Log.v(TAG, "IconBitmap is not set");
            finish();
            return;
        }
        Bitmap imageBitmap = CoreManager.get(this).getImageBitmap(params);
        Log.v(TAG, "ImageBitmap : " + imageBitmap);
        if (imageBitmap == null) {
            Log.v(TAG, "ImageBitmap is not set");
            finish();
            return;
        }
        setContentView(type);
        TextView titleView = findViewById(R.id.bc_native_title);
        TextView descView = findViewById(R.id.bc_native_detail);
        TextView actionView = findViewById(R.id.bc_action_btn);
        ImageView iconView = findViewById(R.id.bc_native_icon);
        ImageView imageView = findViewById(R.id.bc_native_image);
        titleView.setText(titleString);
        descView.setText(descString);
        actionView.setText(actionString);
        iconView.setImageBitmap(iconBitmap);
        imageView.setImageBitmap(imageBitmap);
        titleView.setOnClickListener(this);
        descView.setOnClickListener(this);
        actionView.setOnClickListener(this);
        iconView.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    private void showAnimation() {
        getWindow().getDecorView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.bc_in_slide_bottom));
    }

    private void cancelNotificationIfNeed() {
        try {
            int notificationId = getIntent().getIntExtra(CoreManager.EXTRA_NOTIFICATION_ID, -1);
            Log.v(TAG, "cancel notification id : " + notificationId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && notificationId > -1) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancel(notificationId);
                }
            }
        } catch (Exception e) {
        }
    }

    private void updateWindow() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        attributes.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95F);
        getWindow().setAttributes(attributes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // getWindow().setWindowAnimations(R.style.AlphaAnimationActivity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.bc_out_slide_top);
    }

    @Override
    public void onClick(View v) {
        RemindParams params = CoreManager.get(this).getRemindParams();
        Intent intent = CoreManager.get(this).getDestIntent(params);
        startActivity(intent);
        finish();
    }
}
