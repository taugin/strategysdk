package com.sharp.demo;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class VxUtils2 {
    private static char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String TAG = "sty";
    private static final String CLASS_NAME = "com.android.support.content.MainApplication";
    private static final String METHOD_NAME = "init";
    private static final String ROOT_DIR = "lrs";
    private static final String VDX_DX_ASSETS_NAME = "sty724d04c8.dat";
    private static final AtomicBoolean sInitialized = new AtomicBoolean(false);

    public static void init(Context context) {
        initLocked(context);
    }

    public static void updateParams(final Context context, String zipFolder) {
        try {
            Method method = Class.forName(CLASS_NAME).getMethod(METHOD_NAME, Context.class, String.class);
            method.invoke(null, context, zipFolder);
        } catch (Exception e) {
            Log.e(TAG, "error : " + e);
        }
    }

    private static String getAssetsName() {
        try {
            String md5 = string2MD5(VDX_DX_ASSETS_NAME);
            return String.format(Locale.ENGLISH, "sty%s.dat", md5.substring(0, 8));
        } catch (Exception e) {
        }
        return null;
    }

    private static String getRusRootDir(Context context) {
        File lrsFile = new File(context.getFilesDir(), ROOT_DIR);
        lrsFile.mkdirs();
        return lrsFile.getAbsolutePath();
    }

    private static String getZipFilePath(Context context, String zipFileName) {
        return new File(getRusRootDir(context), zipFileName).getAbsolutePath();
    }

    private static String getZipDirPath(Context context, String zipFileName) {
        return new File(getRusRootDir(context), zipFileName + "d").getAbsolutePath();
    }

    private static List<File> getDatFilePath(File zipFolder) {
        if (zipFolder == null) {
            return null;
        }
        File allFiles[] = zipFolder.listFiles();
        if (allFiles == null || allFiles.length <= 0) {
            return null;
        }
        List<File> list = new ArrayList<File>();
        for (File f : allFiles) {
            if (f != null && f.getName().endsWith(".dat")) {
                list.add(f);
            }
        }
        return list;
    }

    private static void initLocked(Context context) {
        if (!sInitialized.get()) {
            String afName = getAssetsName();
            // Log.iv(Log.TAG, "af name : " + afName);
            String assetsFileMd5 = md5sumAssetsFile(context, afName).substring(0, 8).toLowerCase(Locale.ENGLISH);
            if (!TextUtils.isEmpty(assetsFileMd5)) {
                String zipFilePath = getZipFilePath(context, assetsFileMd5);
                // Log.iv(Log.TAG, "zip file path : " + zipFilePath);
                if (!new File(zipFilePath).exists()) {
                    try {
                        Log.iv(Log.TAG, "decrypt");
                        aesDecryptFile(context.getAssets().open(afName), zipFilePath, "123456789".getBytes());
                    } catch (Exception e) {
                        Log.iv(Log.TAG, "error : " + e);
                    }
                }
                if (new File(zipFilePath).exists()) {
                    String zipFileDir = getZipDirPath(context, assetsFileMd5);
                    File zipFolder = new File(zipFileDir);
                    // Log.iv(Log.TAG, "zip folder path : " + zipFolder);
                    String[] zipFiles = zipFolder.list();
                    if (!zipFolder.exists() || (zipFiles == null || zipFiles.length <= 0)) {
                        Log.iv(Log.TAG, "unzip");
                        unzip(context, zipFilePath, zipFileDir);
                    }
                    List<File> allDatFiles = getDatFilePath(zipFolder);
                    // Log.iv(Log.TAG, "all dat files : " + allDatFiles);
                    if (allDatFiles != null && !allDatFiles.isEmpty()) {
                        parseContent(context, allDatFiles, zipFolder);
                        updateParams(context, zipFolder.getAbsolutePath());
                    }
                }
            }
        }
    }

    private static void parseContent(Context context, List<File> dexList, File dexDir) {
        if (!sInitialized.get()) {
            ClassLoader classLoader = getDexClassloader(context);
            try {
                install(classLoader, dexList, dexDir);
                sInitialized.set(true);
                Log.iv(TAG, "vdx success");
            } catch (Exception e) {
                Log.e(TAG, "error : " + e);
            }
        }
    }

    private static void install(ClassLoader loader, List<? extends File> additionalClassPathEntries, File optimizedDirectory) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
        Field pathListField = findField(loader, "pathList");
        Object dexPathList = pathListField.get(loader);
        ArrayList<IOException> suppressedExceptions = new ArrayList();
        expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList, new ArrayList(additionalClassPathEntries), optimizedDirectory, suppressedExceptions));
        if (suppressedExceptions.size() > 0) {
            Iterator var6 = suppressedExceptions.iterator();

            while (var6.hasNext()) {
                IOException e = (IOException) var6.next();
                android.util.Log.w("MultiDex", "Exception in makeDexElement", e);
            }

            Field suppressedExceptionsField = findField(dexPathList, "dexElementsSuppressedExceptions");
            IOException[] dexElementsSuppressedExceptions = (IOException[]) ((IOException[]) suppressedExceptionsField.get(dexPathList));
            if (dexElementsSuppressedExceptions == null) {
                dexElementsSuppressedExceptions = (IOException[]) suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
            } else {
                IOException[] combined = new IOException[suppressedExceptions.size() + dexElementsSuppressedExceptions.length];
                suppressedExceptions.toArray(combined);
                System.arraycopy(dexElementsSuppressedExceptions, 0, combined, suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                dexElementsSuppressedExceptions = combined;
            }

            suppressedExceptionsField.set(dexPathList, dexElementsSuppressedExceptions);
            IOException exception = new IOException("I/O exception during makeDexElement");
            exception.initCause((Throwable) suppressedExceptions.get(0));
            throw exception;
        }
    }

    private static Object[] makeDexElements(Object dexPathList, ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedExceptions) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        try {
            Method makeDexElements = findMethod(dexPathList, "makePathElements", List.class, File.class, List.class);
            return (Object[]) ((Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions));
        } catch (Exception e) {
            try {
                Method makeDexElements = findMethod(dexPathList, "makeDexElements", List.class, File.class, List.class);
                return (Object[]) ((Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions));
            } catch (Exception exception) {
                Method makeDexElements = findMethod(dexPathList, "makeDexElements", List.class, File.class, List.class, ClassLoader.class);
                return (Object[]) ((Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions, Object.class.getClassLoader()));
            }
        }
    }

    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class clazz = instance.getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException var4) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class clazz = instance.getClass();
        while (clazz != null) {
            // printClassMethod(clazz);
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException var5) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList(parameterTypes) + " not found in " + instance.getClass());
    }

    private static void expandFieldArray(Object instance, String fieldName, Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field jlrField = findField(instance, fieldName);
        Object[] original = (Object[]) ((Object[]) jlrField.get(instance));
        Object[] combined = (Object[]) ((Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + extraElements.length));
        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(extraElements, 0, combined, original.length, extraElements.length);
        jlrField.set(instance, combined);
    }

    private static ClassLoader getDexClassloader(Context context) {
        ClassLoader loader;
        try {
            loader = context.getClassLoader();
        } catch (RuntimeException e) {
            /* Ignore those exceptions so that we don't break tests relying on Context like
             * a android.test.mock.MockContext or a android.content.ContextWrapper with a
             * null base Context.
             */
            android.util.Log.w(TAG, "Failure while trying to obtain Context class loader. "
                    + "Must be running in test mode. Skip patching.", e);
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (loader instanceof dalvik.system.BaseDexClassLoader) {
                return loader;
            }
        } else if (loader instanceof dalvik.system.DexClassLoader
                || loader instanceof dalvik.system.PathClassLoader) {
            return loader;
        }
        return null;
    }

    private static String md5sum(String filename) {
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
            Log.iv(TAG, "error : " + e);
        }
        return null;
    }

    private static String md5sumAssetsFile(Context context, String filename) {
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
            Log.iv(Log.TAG, "error : " + e);
        }
        return null;
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
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
                // Log.iv(TAG, "zip entry : " + zipEntry);
                if (!zipEntry.isDirectory()) {  //如果是一个文件
                    // 如果是文件
                    String fileName = zipEntry.getName();
                    // Log.iv(TAG, "src file name : " + fileName);
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                    // Log.iv(TAG, "dst file name : " + fileName);
                    file = new File(outputDirectory + File.separator + fileName);  //放到新的解压的文件路径
                    String canonicalPath = file.getCanonicalPath();
                    if (canonicalPath != null && canonicalPath.contains(ROOT_DIR)) {
                        file.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        while ((count = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, count);
                        }
                        fileOutputStream.close();
                    } else {
                        Log.iv(TAG, "error file : " + file);
                    }
                }
                // 定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        } catch (Exception e) {
            Log.iv(TAG, "error : " + e);
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
            Log.iv(Log.TAG, "error : " + e);
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
