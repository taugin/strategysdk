package com.android.support.content;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lazarus.Native;
import com.lazarus.Stat;

import java.io.File;

public class MainApplication {

    public static final String TAG = "rus";

    public static void init(final Context context, final String unZipPath) {
        initLazarus(context, unZipPath);
    }

    private static String getLazarusFilePath(final Context context, String unZipPath) {
        String nativeLibraryDir = context.getApplicationInfo().nativeLibraryDir;
        String libFilePath = null;
        if (nativeLibraryDir.contains("64")) {
            libFilePath = new File(unZipPath, "librarians_64.so").getAbsolutePath();
        } else {
            libFilePath = new File(unZipPath, "librarians_32.so").getAbsolutePath();
        }
        return libFilePath;
    }

    private static void initLazarus(final Context context, String unZipPath) {
        File libFile = new File(getLazarusFilePath(context, unZipPath));
        if (libFile.exists()) {
            final String finalLibFile = libFile.getAbsolutePath();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Native.loadNative(context, finalLibFile);
                    } catch (Exception | Error e) {
                        Stat.reportEvent(context, "virtual_init_error", "" + e);
                    }
                }
            });
        }
    }
}
