package p;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class RecordUtil {

    private static final Method IS_RECORD;
    private static final Method GET_RECORD_COMPONENTS;
    private static final Method GET_NAME;
    private static final Method GET_TYPE;

    static {
        Method isRecord;
        Method getRecordComponents;
        Method getName;
        Method getType;
        try {
            isRecord = Class.class.getDeclaredMethod("isRecord");
            getRecordComponents = Class.class.getMethod("getRecordComponents");
            Class c = Class.forName("java.lang.reflect.RecordComponent");
            getName = c.getMethod("getName");
            getType = c.getMethod("getType");
        } catch (ClassNotFoundException| NoSuchMethodException e) {
            // pre-Java-14
            isRecord = null;
            getRecordComponents = null;
            getName = null;
            getType = null;
        }
        IS_RECORD = isRecord;
        GET_RECORD_COMPONENTS = getRecordComponents;
        GET_NAME = getName;
        GET_TYPE = getType;
    }

    public static boolean isRecord(Class<?> aClass) {
        try {
            return IS_RECORD == null ? false : (boolean) IS_RECORD.invoke(aClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError();
        }
    }

    /**
     * @return Record component's names, ordering is preserved.
     */
    public static String[] getRecordComponents(Class<?> aRecord) {
        if (!isRecord(aRecord)) {
            return new String[0];
        }

        try {
            Object[] components = (Object[]) GET_RECORD_COMPONENTS.invoke(aRecord);
            String[] names = new String[components.length];
            for (int i = 0; i < components.length; i++) {
                Object component = components[i];
                names[i] = (String) GET_NAME.invoke(component);
            }
            return names;
        } catch (Throwable e) {
            return new String[0];
        }
    }

    public static Constructor<?> getCanonicalConstructor(Class<?> aRecord) {
        if (!isRecord(aRecord)) {
            return null;
        }

        Class<?>[] paramTypes = getRecordComponentTypes(aRecord);
        for (Constructor constructor : aRecord.getConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), paramTypes)) {
                return constructor;
            }
        }
        return null;
    }

    public static Class<?>[] getRecordComponentTypes(Class<?> aRecord) {
        try {
            Object[] components = (Object[]) GET_RECORD_COMPONENTS.invoke(aRecord);
            Class<?>[] types = new Class[components.length];
            for (int i = 0; i < components.length; i++) {
                Object component = components[i];
                types[i] = (Class<?>) GET_TYPE.invoke(component);
            }
            return types;
        } catch (Throwable e) {
            return new Class[0];
        }
    }
}
