package Reflection;

import java.lang.reflect.Field;

public class StaticIntFieldDef {
    private Field field;

    public StaticIntFieldDef(Class cls, Field field) throws Exception {
        Field declaredField = cls.getDeclaredField(field.getName());
        this.field = declaredField;
        declaredField.setAccessible(true);
    }

    public int get() {
        try {
            return this.field.getInt(null);
        } catch (Exception unused) {
            return 0;
        }
    }

    public void set(int i10) {
        try {
            this.field.setInt(null, i10);
        } catch (Exception unused) {
        }
    }
}
