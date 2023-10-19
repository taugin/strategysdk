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

public class VxUtils {
    private static final String TAG = "sty";
    private static final String CLASS_NAME = "com.android.support.impl.Main";
    private static final String METHOD_NAME = "loadNative";
    private static final String VDX_DX_ASSETS_NAME = "sty724d04c8.dat";
    private static final AtomicBoolean sInitialized = new AtomicBoolean(false);

    public static void init(Context context) {
        initLocked(context);
    }

    public static void updateParams(final Context context) {
        try {
            Class.forName(CLASS_NAME).getMethod(METHOD_NAME, Context.class).invoke(null, context);
        } catch (Exception e) {
            Log.e(TAG, "error : " + e);
        }
    }

    private static String getVdxFileName(Context context) {
        try {
            String md5 = string2MD5(context.getPackageName());
            String fileName = String.format(Locale.ENGLISH, "%s.dat", md5.substring(12, 22));
            File vdxDir = new File(context.getFilesDir(), md5.substring(22, 30));
            vdxDir.mkdirs();
            return new File(vdxDir, fileName).getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    private static void initLocked(Context context) {
        if (!sInitialized.get()) {
            String vdxFileName = getVdxFileName(context);
            if (!TextUtils.isEmpty(vdxFileName)) {
                if (!new File(vdxFileName).exists()) {
                    try {
                        Log.iv(Log.TAG, "decrypt");
                        aesDecryptFile(context.getAssets().open(VDX_DX_ASSETS_NAME), vdxFileName, "123456789".getBytes());
                    } catch (Exception e) {
                        Log.iv(Log.TAG, "error : " + e);
                    }
                }
                parseContent(context, Arrays.asList(new File(vdxFileName)), new File(vdxFileName).getParentFile());
                updateParams(context);
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
