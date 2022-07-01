package com.sharp.component;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import com.sharp.daemon.IBinderManager;
import com.sharp.env.DaemonEntity;
import com.sharp.log.Log;

import java.io.Serializable;
import java.lang.reflect.Field;

public class DaemonMain implements Serializable {
    public static native void lockFile(String str);

    public static native void nativeSetSid();

    public static native void waitFileLock(String str);

    static {
        try {
            // System.setProperty("REGISTER_CLASS_PATH", DaemonMain.class.getName().replaceAll(".", "/"));
            System.loadLibrary("sharp_daemon");
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e, e);
        }
    }

    IBinderManager mBinderManager = new IBinderManager();
    /* access modifiers changed from: private */
    public DaemonEntity daemonEntity;

    /* renamed from: c  reason: collision with root package name */
    private Parcel mServiceParcel;
    private Parcel mBroadcastParcel;
    private Parcel mInstrumentParcel;
    private IBinder mBinder;

    public DaemonMain(DaemonEntity daemonEntity) {
        this.daemonEntity = daemonEntity;
    }

    public static void main(String[] strArr) {
        DaemonEntity entity = DaemonEntity.toObject(strArr[0]);
        if (entity != null) {
            new DaemonMain(entity).run();
        }
        Process.killProcess(Process.myPid());
    }

    private void run() {
        Log.v(Log.TAG, "daemon main run start [" + daemonEntity.processName + "]");
        try {
            setBinder();
            fillAllParcel();
            DaemonMain.nativeSetSid();
            try {
                Log.v(Log.TAG, "setargv0 " + this.daemonEntity.processName);
                Process.class.getMethod("setArgV0", new Class[]{String.class}).invoke((Object) null, new Object[]{this.daemonEntity.processName});
            } catch (Exception e2) {
            }
            for (int i = 1; i < this.daemonEntity.daemonPath.length; i++) {
                new DaemonThread(i).start();
            }
            Log.v(Log.TAG, this.daemonEntity.processName + " start lock file : " + this.daemonEntity.daemonPath[0]);
            DaemonMain.waitFileLock(this.daemonEntity.daemonPath[0]);
            Log.v(Log.TAG, "lock File finish");
            startService();
            sendBroadcast();
            startInstrumentation();
            Log.v(Log.TAG, "start android finish");
            Log.v(Log.TAG, "daemon main run end [" + daemonEntity.processName + "]");
        } catch (Exception e3) {
            mBinderManager.b(e3);
        }
    }

    /* access modifiers changed from: private */
    public void startInstrumentation() {
        if (this.mInstrumentParcel != null) {
            try {
                this.mBinder.transact(this.mBinderManager.getInstrumentationTransaction(), this.mInstrumentParcel, (Parcel) null, 1);
            } catch (Exception e2) {
                mBinderManager.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendBroadcast() {
        if (this.mBroadcastParcel != null) {
            try {
                this.mBinder.transact(this.mBinderManager.getBroadcastTransaction(), this.mBroadcastParcel, (Parcel) null, 1);
            } catch (Exception e2) {
                mBinderManager.b(e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void startService() {
        if (this.mServiceParcel != null) {
            try {
                this.mBinder.transact(this.mBinderManager.getStartServiceTransaction(), this.mServiceParcel, (Parcel) null, 1);
            } catch (Exception e2) {
                mBinderManager.b(e2);
            }
        }
    }

    private void fillServiceParcel() {
        this.mServiceParcel = Parcel.obtain();
        this.mServiceParcel.writeInterfaceToken("android.app.IActivityManager");
        this.mServiceParcel.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.mServiceParcel.writeInt(1);
        }
        this.daemonEntity.serviceIntent.writeToParcel(this.mServiceParcel, 0);
        this.mServiceParcel.writeString((String) null);
        if (Build.VERSION.SDK_INT >= 26) {
            // 参数为0，表示用startService启动service
            // 参数为1，表示用startForegroundService启动service
            this.mServiceParcel.writeInt(1);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            this.mServiceParcel.writeString(this.daemonEntity.serviceIntent.getComponent().getPackageName());
        }
        this.mServiceParcel.writeInt(0);
    }

    private void fillAllParcel() {
        fillServiceParcel();
        fillBroadcastParcel();
        fillInstrumentParcel();
    }

    private void fillBroadcastParcel() {
        this.mBroadcastParcel = Parcel.obtain();
        this.mBroadcastParcel.writeInterfaceToken("android.app.IActivityManager");
        this.mBroadcastParcel.writeStrongBinder((IBinder) null);
        if (Build.VERSION.SDK_INT >= 26) {
            this.mBroadcastParcel.writeInt(1);
        }

        this.daemonEntity.broadcastIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        this.daemonEntity.broadcastIntent.writeToParcel(this.mBroadcastParcel, 0);
        this.mBroadcastParcel.writeString((String) null);
        this.mBroadcastParcel.writeStrongBinder((IBinder) null);
        this.mBroadcastParcel.writeInt(-1);
        this.mBroadcastParcel.writeString((String) null);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeStringArray((String[]) null);
        this.mBroadcastParcel.writeInt(-1);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeInt(0);
        this.mBroadcastParcel.writeInt(0);
    }

    private void fillInstrumentParcel() {
        this.mInstrumentParcel = Parcel.obtain();
        this.mInstrumentParcel.writeInterfaceToken("android.app.IActivityManager");
        if (Build.VERSION.SDK_INT >= 26) {
            this.mInstrumentParcel.writeInt(1);
        }
        this.daemonEntity.instrumentIntent.getComponent().writeToParcel(this.mInstrumentParcel, 0);
        this.mInstrumentParcel.writeString((String) null);
        this.mInstrumentParcel.writeInt(0);
        this.mInstrumentParcel.writeInt(0);
        this.mInstrumentParcel.writeStrongBinder((IBinder) null);
        this.mInstrumentParcel.writeStrongBinder((IBinder) null);
        this.mInstrumentParcel.writeInt(0);
        this.mInstrumentParcel.writeString((String) null);
    }

    private void setBinder() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityManagerNative");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(cls, new Object[0]);
            Field declaredField = invoke.getClass().getDeclaredField("mRemote");
            declaredField.setAccessible(true);
            this.mBinder = (IBinder) declaredField.get(invoke);
            IBinder iBinder = (IBinder) Class.forName("android.os.ServiceManager").getMethod("getService", new Class[]{String.class}).invoke((Object) null, new Object[]{"activity"});
            Log.v(Log.TAG, "initAmsBinder: mRemote == iBinder " + this.mBinder);
        } catch (Throwable th) {
            mBinderManager.b(th);
        }
    }

    public class DaemonThread extends Thread {
        private int mIndex;

        public DaemonThread(int i) {
            this.mIndex = i;
        }

        public void run() {
            Log.v(Log.TAG, "daemon thread run start [" + daemonEntity.processName + "]");
            setPriority(10);
            DaemonMain.waitFileLock(DaemonMain.this.daemonEntity.daemonPath[this.mIndex]);
            Log.v(Log.TAG, "Thread lock File finish");
            DaemonMain.this.startService();
            DaemonMain.this.sendBroadcast();
            DaemonMain.this.startInstrumentation();
            Log.v(Log.TAG, "Thread start android finish");
            Log.v(Log.TAG, "Thread  exit ");
            Log.v(Log.TAG, "daemon thread run end [" + daemonEntity.processName + "]");
        }
    }
}
