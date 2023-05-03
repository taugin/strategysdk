package Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectUtils {
    public static Field findField(Object obj, String str) throws NoSuchFieldException {
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            try {
                Field declaredField = cls.getDeclaredField(str);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchFieldException unused) {
            }
        }
        StringBuilder h7 = new StringBuilder();
        h7.append("Field").append(str).append(" not found in ");
        h7.append(obj.getClass());
        throw new NoSuchFieldException(h7.toString());
    }

    public static Method findMethod(Object obj, String str, Class<?>... clsArr) throws NoSuchMethodException {
        for (Class<?> cls = obj.getClass(); cls != null; cls = cls.getSuperclass()) {
            try {
                Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
                if (!declaredMethod.isAccessible()) {
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            } catch (NoSuchMethodException unused) {
            }
        }
        StringBuilder h7 = new StringBuilder();
        h7.append("Method").append(str).append(" with parameters ");
        h7.append(Arrays.asList(clsArr));
        h7.append(" not found in ");
        h7.append(obj.getClass());
        throw new NoSuchMethodException(h7.toString());
    }

    public static Field findField(String str, String str2) throws NoSuchFieldException, ClassNotFoundException {
        for (Class<?> cls = Class.forName(str); cls != null; cls = cls.getSuperclass()) {
            try {
                Field declaredField = cls.getDeclaredField(str2);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchFieldException unused) {
            }
        }
        throw new NoSuchFieldException("Field " + str + " not found");
    }

    public static Method findMethod(String str, String str2, Class<?>... clsArr) throws NoSuchMethodException, ClassNotFoundException {
        for (Class<?> cls = Class.forName(str); cls != null; cls = cls.getSuperclass()) {
            try {
                Method declaredMethod = cls.getDeclaredMethod(str2, clsArr);
                if (!declaredMethod.isAccessible()) {
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            } catch (NoSuchMethodException unused) {
            }
        }
        StringBuilder h7 = new StringBuilder();
        h7.append("Method").append(str).append(" with parameters ");
        h7.append(Arrays.asList(clsArr));
        h7.append(" not found in ");
        h7.append(str);
        throw new NoSuchMethodException(h7.toString());
    }
}