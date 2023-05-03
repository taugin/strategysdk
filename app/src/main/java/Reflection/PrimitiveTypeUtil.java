package Reflection;

public class PrimitiveTypeUtil {
    public static final Class<?> getPrimitiveClass(String str) {
        if (str.equals("int")) {
            return Integer.TYPE;
        }
        if (str.equals("long")) {
            return Long.TYPE;
        }
        if (str.equals("boolean")) {
            return Boolean.TYPE;
        }
        if (str.equals("byte")) {
            return Byte.TYPE;
        }
        if (str.equals("short")) {
            return Short.TYPE;
        }
        if (str.equals("char")) {
            return Character.TYPE;
        }
        if (str.equals("float")) {
            return Float.TYPE;
        }
        if (str.equals("double")) {
            return Double.TYPE;
        }
        if (str.equals("void")) {
            return Void.TYPE;
        }
        return null;
    }

    public static final boolean isPrimitive(String str) {
        if (str == null) {
            return false;
        }
        return str.equals("byte") || str.equals("short") || str.equals("int") || str.equals("long") || str.equals("char") || str.equals("float") || str.equals("double") || str.equals("boolean") || str.equals("void");
    }
}
