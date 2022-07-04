package com.sharp.model;

import android.graphics.Bitmap;
import android.os.Bundle;

public abstract class Params {
    public abstract int getNotificationLayout();

    public abstract int getSmallIcon();

    public abstract int getIconId();

    public abstract Bitmap getIconBitmap() ;

    public abstract int getTitleId();

    public abstract String getTitleString();

    public abstract int getDescId();

    public abstract String getDescString();

    public abstract int getActionId();

    public abstract String getActionString();

    public abstract Bundle getBundle();

    public abstract Class<?> getStartClass();
}
