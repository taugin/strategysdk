package com.sharp.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.MessageDigest;

public class Utils {
    private static char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    public static String getCmdLine() {
        try {
            return new BufferedReader(new FileReader(new File("/proc/self/cmdline"))).readLine().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static void startServiceOrBindService(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        try {
            context.startService(intent);
        } catch (Exception e) {
            context.bindService(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                }

                public void onServiceDisconnected(ComponentName componentName) {
                }
            }, 0);
        }
    }

    public static void putString(Context context, String key, String value) {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
        } catch (Exception | Error e) {
        }
    }

    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }


    public static void putLong(Context context, String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, 0);
    }

    public static long getLong(Context context, String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, defValue);
    }

    public static void copyAssets(Context context, String fileName, String dstPath) {
        try {
            if (TextUtils.equals(md5sumAssetsFile(context, fileName), md5sum(dstPath))) {
                return;
            }
            InputStream is = context.getAssets().open(fileName);
            File dstFile = new File(dstPath);
            if (dstFile.exists()) {
                dstFile.delete();
            }
            dstFile.getParentFile().mkdirs();
            dstFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(dstPath);
            byte[] buf = new byte[4096];
            int read = 0;
            while ((read = is.read(buf)) > 0) {
                fos.write(buf, 0, read);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            Log.d(Log.TAG, "error : " + e);
        }
    }

    public static String md5sum(String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5 = null;
        try {
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            Log.d(Log.TAG, "error : " + e);
        }
        return null;
    }

    public static String md5sumAssetsFile(Context context, String filename) {
        InputStream fis;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5 = null;
        try {
            fis = context.getAssets().open(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fis.close();
            return toHexString(md5.digest());
        } catch (Exception e) {
            Log.d(Log.TAG, "error : " + e);
        }
        return null;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
}
