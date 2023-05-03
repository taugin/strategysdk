package com.lazarus;

import android.accounts.Account;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SyncResult;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class Native {
    public static class g {
        public static native void a(Context context, DisplayManager displayManager);
    }

    /**
    public static class b {
        public static native long a(Application application, String[] strArr, String str, boolean z9, String str2, boolean z10, boolean z11, boolean z12, boolean z13, String str3, String str4, String str5, ComponentName componentName, int i10, int i11, int i12, byte[] bArr, Handler handler, String str6, String str7, Class<? extends android.app.Service> cls, ServiceConnection serviceConnection, int i13, byte[][] bArr2);

        public static native ComponentName a(Application application, String[] strArr, int[] iArr);

        public static native void a();

        public static native void a(long j10, boolean z9);

        public static native void a(long j10, boolean z9, boolean z10, boolean z11, boolean z12, boolean z13);

        public static native void a(ClassLoader classLoader);

        public static native void a(String str, String str2);

        public static native void b(long j10);

        public static native void c(long j10);

        public static native int d(long j10);

        public static native int e(long j10);

        public static native void f(long j10);

        public static native PendingIntent g(long j10);

        public static native int h();

    }

    public static class c {
        public static native Bundle a(long j10, String str, String str2, Bundle bundle);
    }

    public static class d {
        public static native IBinder a();

        public static native void a(IBinder iBinder);

    }

    public static class e {
        public static native void a();

        public static native void a(int i10);

        public static native void a(int i10, int i11);

        public static native void a(String str, int i10, Runnable runnable);

    }

    public static class f {
        public static native void a(Account account, String str, boolean z9);

        public static native void a(SyncResult syncResult, long j10);

        public static native void a(String str, String str2, String str3);

        public static native void b(String str, String str2, String str3);

    } */
}
