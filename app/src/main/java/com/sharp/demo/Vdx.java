package com.sharp.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Vdx {
    private static final String TAG = "vdx";
    private static final String CLASS_NAME = "com.sharp.vdx.VdxAction";
    private static final String METHOD_NAME = "execute";
    private static final String METHOD_NAME_RESULT = "sendResult";
    private static final String VDX_DEX_PATH = "vdx";
    private static final String VDX_DEX_NAME = "vdx.dex";
    private static final String ACTION_START_COMPLETE = "list_action";
    private static AtomicBoolean sLoadDex = new AtomicBoolean(false);

    public static void loadDex(Context context) {
        copyDexFile(context);
    }

    public static void startActivity(Context context, Intent intent) {
        try {
            Method method = Class.forName(CLASS_NAME).getMethod(METHOD_NAME, new Class[]{Context.class, Intent.class});
            method.invoke(null, context, intent);
        } catch (Exception e) {
            Log.e(TAG, "error : " + e, e);
        }
    }

    public static void sendResult(Context context, Intent intent) {
        try {
            String action = intent.getStringExtra(ACTION_START_COMPLETE);
            Log.v(TAG, "start received action = " + action);
            if (!TextUtils.isEmpty(action)) {
                try {
                    Method method = Class.forName(CLASS_NAME).getMethod(METHOD_NAME_RESULT, new Class[]{Context.class, String.class});
                    method.invoke(null, context, action);
                } catch (Exception e) {
                    Log.e(TAG, "error : " + e, e);
                }
            }
        } catch (Exception e) {
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
            ClassLoader classLoader = getDexClassloader(context);
            List<File> dexList = new ArrayList<>();
            dexList.add(new File(dexFile));
            File dexDir = getFinalDexDir(context);
            try {
                install(classLoader, dexList, dexDir);
                sLoadDex.set(true);
                Log.v(TAG, "vdx success");
            } catch (Exception e) {
                Log.e(TAG, "error : " + e, e);
            }
        }
    }


    static void install(ClassLoader loader, List<? extends File> additionalClassPathEntries, File optimizedDirectory) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
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

    public static ClassLoader getDexClassloader(Context context) {
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
        android.util.Log.e(TAG, "Context class loader is null or not dex-capable. "
                + "Must be running in test mode. Skip patching.");
        return null;
    }

    private static void printClassMethod(Class clazz) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            android.util.Log.v(TAG, "class name : " + clazz.getName() + "\n===================");
            for (Method m : methods) {
                android.util.Log.v(TAG, "name : " + m.getName() + " , args : " + Arrays.asList(m.getGenericParameterTypes()));
            }
        } catch (Exception e) {
        }
    }
}
