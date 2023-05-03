package Reflection;

import java.lang.reflect.Field;

public class StaticFieldDef<T> {
    private Field field;

    public StaticFieldDef(Class cls, Field field) throws Exception {
        Field declaredField = cls.getDeclaredField(field.getName());
        this.field = declaredField;
        declaredField.setAccessible(true);
    }

    public T get() {
        try {
            return (T) this.field.get(null);
        } catch (Exception unused) {
            return null;
        }
    }

    public void set(T t9) {
        try {
            this.field.set(null, t9);
        } catch (Exception unused) {
        }
    }
}
