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

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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

    public static void copyAssetFile(Context context, String fileName, String dstPath) {
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

    public static void copyFile(String fileName, String dstPath) {
        try {
            if (TextUtils.equals(md5sum(fileName), md5sum(dstPath))) {
                return;
            }
            InputStream is = new FileInputStream(fileName);
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

    private static byte[] getRawKey(byte[] key) throws Exception {
        byte[] keyByte = new byte[16];
        if (key == null)
            throw new IllegalArgumentException("seed == null");
        if (key.length == 0)
            throw new IllegalArgumentException("seed.length == 0");
        if (key.length < 16) {
            int i = 0;
            while (i < keyByte.length) {
                if (i < key.length) {
                    keyByte[i] = key[i];
                } else {
                    keyByte[i] = 0;
                }
                i++;
            }
        }
        return keyByte;
    }

    /**
     * AES加密
     *
     * @param sourceFile  源文件
     * @param encryptFile 加密文件
     * @param password    密钥,128bit
     * @throws Exception 抛出异常
     */
    public static void aesEncryptFile(String sourceFile, String encryptFile, byte[] password) throws Exception {
        // 创建AES密钥
        SecretKeySpec key = new SecretKeySpec(getRawKey(password), "AES");
        // 创建加密引擎(CBC模式)。Cipher类支持DES，DES3，AES和RSA加加密
        // AES：算法名称
        // CBC：工作模式
        // PKCS5Padding：明文块不满足128bits时填充方式（默认），即在明文块末尾补足相应数量的字符，
        // 且每个字节的值等于缺少的字符数。另外一种方式是ISO10126Padding，除最后一个字符值等于少的字符数
        // 其他字符填充随机数。
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 初始化加密器
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        // 原始文件流
        FileInputStream inputStream = new FileInputStream(sourceFile);
        // 加密文件流
        FileOutputStream outputStream = new FileOutputStream(encryptFile);
        // 以加密流写入文件
        CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
        byte[] tmpArray = new byte[1024];
        int len;
        while ((len = cipherInputStream.read(tmpArray)) != -1) {
            outputStream.write(tmpArray, 0, len);
            outputStream.flush();
        }
        cipherInputStream.close();
        inputStream.close();
        outputStream.close();
    }

    /**
     * AES解密
     *
     * @param encryptFile 加密文件
     * @param decryptFile 解密文件
     * @param password    密钥，128bit *
     * @throws Exception 抛出异常
     */
    public static void aesDecryptFile(String encryptFile, String decryptFile, byte[] password) throws Exception {
        // 创建AES密钥，即根据一个字节数组构造一个SecreteKey
        // 而这个SecreteKey是符合指定加密算法密钥规范
        SecretKeySpec key = new SecretKeySpec(getRawKey(password), "AES");
        // 创建解密引擎(CBC模式)
        // Cipher类支持DES，DES3，AES和RSA加解密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        // 加密文件流
        FileInputStream fileInputStream = new FileInputStream(encryptFile);
        // 解密文件流
        FileOutputStream fileOutputStream = new FileOutputStream(decryptFile);
        // 以解密流写出文件
        CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fileInputStream.read(buffer)) >= 0) {
            cipherOutputStream.write(buffer, 0, len);
        }
        cipherOutputStream.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
