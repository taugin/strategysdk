package com.sharp.model;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.sharp.future.R;

import java.io.Serializable;

public class RemindParams extends Params implements Serializable {
    public static final int LAYOUT_REMIND_1 = R.layout.bc_remind_layout_1;
    public static final int LAYOUT_REMIND_2 = R.layout.bc_remind_layout_2;

    private int notificationLayout = LAYOUT_REMIND_1;
    private int smallIcon;
    private int iconId;
    private Bitmap iconBitmap;
    private int titleId;
    private String titleString;
    private int descId;
    private String descString;
    private int imageId;
    private Bitmap imageBitmap;
    private int actionId;
    private String actionString;
    private Bitmap actionBitmap;
    private Bundle bundle;
    private Class<?> startClass;
    private RemoteViews remoteViews;
    private int notificationId = -1;

    private RemindParams() {
    }

    @Override
    public int getNotificationLayout() {
        return notificationLayout;
    }

    @Override
    public int getSmallIcon() {
        return smallIcon;
    }

    @Override
    public int getIconId() {
        return iconId;
    }

    @Override
    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public int getTitleId() {
        return titleId;
    }

    public String getTitleString() {
        return titleString;
    }

    @Override
    public int getDescId() {
        return descId;
    }

    @Override
    public String getDescString() {
        return descString;
    }

    public int getImageId() {
        return imageId;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    @Override
    public int getActionId() {
        return actionId;
    }

    @Override
    public String getActionString() {
        return actionString;
    }

    public Bitmap getActionBitmap() {
        return actionBitmap;
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public Class<?> getStartClass() {
        return startClass;
    }

    @Override
    public RemoteViews getRemoteViews() {
        return remoteViews;
    }

    public int getNotificationId() {
        return notificationId;
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
        private int imageId;
        private Bitmap imageBitmap;
        private int actionId;
        private String actionString;
        private Bitmap actionBitmap;
        private Bundle bundle;
        private Class<?> startClass;
        private RemoteViews remoteViews;
        private int notificationId = -1;

        public Builder setLayoutType(int layoutType) {
            this.layoutType = layoutType;
            return this;
        }

        public Builder setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
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

        public Builder setImageId(int imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder setImageBitmap(Bitmap imageBitmap) {
            this.imageBitmap = imageBitmap;
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

        public Builder setActionBitmap(Bitmap actionBitmap) {
            this.actionBitmap = actionBitmap;
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

        public Builder setRemoteViews(RemoteViews remoteViews) {
            this.remoteViews = remoteViews;
            return this;
        }

        public Builder setNotificationId(int notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public RemindParams build() {
            RemindParams params = new RemindParams();
            params.notificationLayout = layoutType;
            params.smallIcon = smallIcon;
            params.iconId = iconId;
            params.iconBitmap = iconBitmap;
            params.titleId = titleId;
            params.titleString = titleString;
            params.descId = descId;
            params.descString = descString;
            params.imageId = imageId;
            params.imageBitmap = imageBitmap;
            params.actionId = actionId;
            params.actionString = actionString;
            params.actionBitmap = actionBitmap;
            params.bundle = bundle;
            params.startClass = startClass;
            params.remoteViews = remoteViews;
            params.notificationId = notificationId;
            return params;
        }
    }
}
