package com.sharp.demo;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Vdx {
    private static final String TAG = "vdx";
    private static final String CLASS_NAME = "com.sharp.vdx.VdxAction";
    private static final String METHOD_NAME = "execute";
    private static final String VDX_DEX_PATH = "vdx";
    private static final String VDX_DEX_NAME = "vdx.dex";
    private static AtomicBoolean sLoadDex = new AtomicBoolean(false);

    public static void loadDex(Context context) {
        String dexFilePath = getFinalDexPath(context);
        if (!TextUtils.isEmpty(dexFilePath) && new File(dexFilePath).exists()) {
            loadDexFile(context, dexFilePath);
        } else {
            copyDexFile(context);
        }
    }

    public static void startActivity(Context context, Intent intent) {
        try {
            Method method = Class.forName(CLASS_NAME).getMethod(METHOD_NAME, new Class[]{Context.class, Intent.class});
            method.invoke(null, context, intent);
        } catch (Exception e) {
            Log.e(TAG, "error : " + e, e);
        }
    }

    private static String getFinalDexPath(Context context) {
        return new File(context.getFilesDir(), VDX_DEX_PATH).getAbsolutePath();
    }

    private static File getFinalDexDir(Context context) {
        return new File(context.getFilesDir(), VDX_DEX_NAME);
    }

    private static void copyDexFile(Context context) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String dexFilePath = getFinalDexPath(context);
                Utils.copyAssets(context, "classes-jar2dex.dex", dexFilePath);
                loadDexFile(context, dexFilePath);
            }
        });
    }

    private static void loadDexFile(Context context, String dexFile) {
        if (!sLoadDex.get()) {
            ClassLoader classLoader = VdxDex.getDexClassloader(context);
            List<File> dexList = new ArrayList<>();
            dexList.add(new File(dexFile));
            File dexDir = getFinalDexDir(context);
            try {
                VdxDex.install(classLoader, dexList, dexDir);
                sLoadDex.set(true);
            } catch (Exception e) {
                Log.e(TAG, "error : " + e, e);
            }
        }
    }
}
