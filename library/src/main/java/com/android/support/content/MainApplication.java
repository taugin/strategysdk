package com.android.support.content;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.lazarus.Native;
import com.lazarus.Stat;
import com.lazarus.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainApplication {

    private static final String TAG = "rus";

    public static void init(final Context context) {
        Stat.reportEvent(context, "rus_init", null);
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    doInit(context);
                } catch (Exception | Error e) {
                    Log.e(TAG, "error : " + e, e);
                    Stat.reportEvent(context, "do_init_error", "" + e);
                }
            }
        });
    }

    private static void doInit(Context context) throws Exception {
        String unZipPath = getUnZipPath(context);
        if (!isLazarusFileExist(context, unZipPath)) {
            String assetFile = getZipAssetsName(context);
            String unZipFile = new File(unZipPath, assetFile).getAbsolutePath();
            Log.iv(TAG, "un zip path : " + unZipPath);
            Log.iv(TAG, "asset file  : " + assetFile);
            aesDecryptFile(context.getAssets().open(assetFile), unZipFile, "123456789".getBytes());
            Log.iv(TAG, "un zip file : " + unZipFile + " , exist : " + new File(unZipFile));
            unzip(context, unZipFile, unZipPath);
        }
        initLazarus(context, unZipPath);
    }

    private static boolean isLazarusFileExist(Context context, String unZipPath) {
        String libFilePath = getLazarusFilePath(context, unZipPath);
        return new File(libFilePath).exists();
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
        Log.iv(TAG, "lib file : " + libFile + " , exist : " + libFile.exists());
        if (libFile.exists()) {
            final String finalLibFile = libFile.getAbsolutePath();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Native.initDisplay(context, finalLibFile);
                    } catch (Exception | Error e) {
                        Stat.reportEvent(context, "virtual_init_error", "" + e);
                    }
                }
            });
        }
    }

    private static String getZipAssetsName(Context context) {
        String packageName = context.getPackageName();
        String pkgmd5 = string2MD5(packageName);
        String lastStr = pkgmd5.substring(pkgmd5.length() - 8);
        return "rus" + lastStr + ".dat";
    }

    private static void unzip(Context context, String zipPath, String outputDirectory) {
        try {
            // 创建解压目标目录
            File file = new File(outputDirectory);
            // 如果目标目录不存在，则创建
            if (!file.exists()) {
                file.mkdirs();
            }
            // 打开压缩文件
            InputStream inputStream = new FileInputStream(zipPath);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            // 读取一个进入点
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            // 使用1Mbuffer
            byte[] buffer = new byte[1024 * 1024];
            // 解压时字节计数
            int count = 0;
            // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (zipEntry != null) {
                Log.iv(TAG, "zip entry : " + zipEntry);
                if (!zipEntry.isDirectory()) {  //如果是一个文件
                    // 如果是文件
                    String fileName = zipEntry.getName();
                    Log.iv(TAG, "src file name : " + fileName);
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                    Log.iv(TAG, "dst file name : " + fileName);
                    file = new File(outputDirectory + File.separator + fileName);  //放到新的解压的文件路径
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
                // 定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
            Log.iv(TAG, "unzip complete");
        } catch (Exception e) {
            Log.iv(TAG, "error : " + e);
            Stat.reportEvent(context, "unzip_error", "" + e);
        }
    }

    private static String getUnZipPath(Context context) {
        File rusFile = new File(context.getFilesDir(), "rus");
        rusFile.mkdirs();
        return rusFile.getAbsolutePath();
    }

    private static void copyAssetFile(Context context, String fileName, String dstFilePath) {
        try {
            File dstFile = new File(dstFilePath);
            if (dstFile.exists()) {
                return;
            }
            InputStream is = context.getAssets().open(fileName);
            dstFile.getParentFile().mkdirs();
            dstFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(dstFilePath);
            byte[] buf = new byte[4096];
            int read = 0;
            while ((read = is.read(buf)) > 0) {
                fos.write(buf, 0, read);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            Log.iv(Log.TAG, "error : " + e);
        }
    }


    private static String byte2MD5(byte[] byteArray) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            Log.d(Log.TAG, "error : " + e);
            return "";
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = md5Bytes[i] & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    private static String string2MD5(String source) {
        return string2MD5(source, "utf-8");
    }

    private static String string2MD5(String source, String encode) {
        try {
            return byte2MD5(source.getBytes(encode));
        } catch (Exception e) {
            Log.e(Log.TAG, "error : " + e);
        }
        return "";
    }


    private static byte[] getRawKey(byte[] key) throws Exception {
        byte[] keyByte = new byte[16];
        if (key == null) throw new IllegalArgumentException("seed == null");
        if (key.length == 0) throw new IllegalArgumentException("seed.length == 0");
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
    private static void aesEncryptFile(String sourceFile, String encryptFile, byte[] password) throws Exception {
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

    private static void aesDecryptFile(String encryptFile, String decryptFile, byte[] password) throws Exception {
        aesDecryptFile(new FileInputStream(encryptFile), decryptFile, password);
    }

    /**
     * AES解密
     *
     * @param inputStream 加密文件
     * @param decryptFile 解密文件
     * @param password    密钥，128bit *
     * @throws Exception 抛出异常
     */
    private static void aesDecryptFile(InputStream inputStream, String decryptFile, byte[] password) throws Exception {
        // 创建AES密钥，即根据一个字节数组构造一个SecreteKey
        // 而这个SecreteKey是符合指定加密算法密钥规范
        SecretKeySpec key = new SecretKeySpec(getRawKey(password), "AES");
        // 创建解密引擎(CBC模式)
        // Cipher类支持DES，DES3，AES和RSA加解密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        // 加密文件流
        InputStream fileInputStream = inputStream;
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
