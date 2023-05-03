package Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StaticMethodDef<T> {
    private Method method;

    public StaticMethodDef(Class<?> cls, Field field) throws Exception {
        if (field.isAnnotationPresent(MethodInfo.class)) {
            Method declaredMethod = cls.getDeclaredMethod(field.getName(), ((MethodInfo) field.getAnnotation(MethodInfo.class)).value());
            this.method = declaredMethod;
            declaredMethod.setAccessible(true);
            return;
        }
        int i10 = 0;
        if (field.isAnnotationPresent(MethodReflectionInfo.class)) {
            String[] value = ((MethodReflectionInfo) field.getAnnotation(MethodReflectionInfo.class)).value();
            Class<?>[] clsArr = new Class[value.length];
            while (i10 < value.length) {
                Class<?> primitiveClass = PrimitiveTypeUtil.getPrimitiveClass(value[i10]);
                if (primitiveClass == null) {
                    clsArr[i10] = Class.forName(value[i10]);
                } else {
                    clsArr[i10] = primitiveClass;
                }
                i10++;
            }
            Method declaredMethod2 = cls.getDeclaredMethod(field.getName(), clsArr);
            this.method = declaredMethod2;
            declaredMethod2.setAccessible(true);
            return;
        }
        Method[] declaredMethods = cls.getDeclaredMethods();
        int length = declaredMethods.length;
        while (true) {
            if (i10 >= length) {
                break;
            }
            Method method = declaredMethods[i10];
            if (method.getName().equals(field.getName())) {
                this.method = method;
                method.setAccessible(true);
                break;
            }
            i10++;
        }
        if (this.method == null) {
            throw new NoSuchMethodException(field.getName());
        }
    }

    public T invoke(Object... objArr) {
        try {
            return (T) this.method.invoke(null, objArr);
        } catch (Exception unused) {
            return null;
        }
    }

    public T invokeWithException(Object... objArr) throws Exception {
        return (T) this.method.invoke(null, objArr);
    }
}
