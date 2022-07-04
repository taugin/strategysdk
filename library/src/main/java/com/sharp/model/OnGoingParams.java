package com.sharp.model;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.sharp.future.R;

import java.io.Serializable;

public class OnGoingParams extends Params implements Serializable {
    public static final int LAYOUT_ONGOING_1 = R.layout.bc_ongoing_layout_1;
    private int notificationLayout;
    private int smallIcon;
    private int iconId;
    private Bitmap iconBitmap;
    private int titleId;
    private String titleString;
    private int descId;
    private String descString;
    private int actionId;
    private String actionString;
    private Bundle bundle;
    private Class<?> startClass;

    private OnGoingParams() {
    }

    public int getNotificationLayout() {
        return notificationLayout;
    }

    public int getSmallIcon() {
        return smallIcon;
    }

    public int getIconId() {
        return iconId;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public int getTitleId() {
        return titleId;
    }

    public String getTitleString() {
        return titleString;
    }

    public int getDescId() {
        return descId;
    }

    public String getDescString() {
        return descString;
    }

    public int getActionId() {
        return actionId;
    }

    public String getActionString() {
        return actionString;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Class<?> getStartClass() {
        return startClass;
    }

    public static class Builder {
        private int layoutType;
        private int smallIcon;
        private int iconId;
        private Bitmap iconBitmap;
        private int titleId;
        private String titleString;
        private int descId;
        private String descString;
        private int actionId;
        private String actionString;
        private Bundle bundle;
        private Class<?> startClass;

        public OnGoingParams build() {
            OnGoingParams params = new OnGoingParams();
            params.notificationLayout = layoutType;
            params.smallIcon = smallIcon;
            params.iconId = iconId;
            params.iconBitmap = iconBitmap;
            params.titleId = titleId;
            params.titleString = titleString;
            params.descId = descId;
            params.descString = descString;
            params.actionId = actionId;
            params.actionString = actionString;
            params.bundle = bundle;
            params.startClass = startClass;
            return params;
        }

        public Builder setLayoutType(int layoutType) {
            this.layoutType = layoutType;
            return this;
        }

        public void setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
        }

        public Builder setIconId(int iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder setIconBitmap(Bitmap iconBitmap) {
            this.iconBitmap = iconBitmap;
            return this;
        }

        public Builder setTitleId(int titleId) {
            this.titleId = titleId;
            return this;
        }

        public Builder setTitleString(String titleString) {
            this.titleString = titleString;
            return this;
        }

        public Builder setDescId(int descId) {
            this.descId = descId;
            return this;
        }

        public Builder setDescString(String descString) {
            this.descString = descString;
            return this;
        }

        public Builder setActionId(int actionId) {
            this.actionId = actionId;
            return this;
        }

        public Builder setActionString(String actionString) {
            this.actionString = actionString;
            return this;
        }

        public Builder setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder setStartClass(Class<?> startClass) {
            this.startClass = startClass;
            return this;
        }
    }
}
