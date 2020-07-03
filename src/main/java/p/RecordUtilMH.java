package p;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import static java.lang.invoke.MethodType.methodType;

public class RecordUtilMH {
    private static final MethodHandle IS_RECORD_HANDLE;
    private static final MethodHandle GET_RECORD_COMPONENTS_HANDLE;
    private static final MethodHandle GET_NAME_HANDLE;
    private static final MethodHandle GET_TYPE_HANDLE;

    static {
        MethodHandle isRecordHandle;
        MethodHandle getRecordComponentsHandle;
        MethodHandle getNameHandle;
        MethodHandle getTypeHandle;
        try {
            Class c = Class.forName("java.lang.reflect.RecordComponent");
            var lookup = MethodHandles.lookup();
            isRecordHandle = lookup.findVirtual(Class.class, "isRecord", methodType(boolean.class));
            getRecordComponentsHandle = lookup.findVirtual(Class.class, "getRecordComponents", methodType(c.arrayType()))
                    .asType(methodType(Object[].class, Class.class));
            getNameHandle = lookup.findVirtual(c, "getName", methodType(String.class))
                    .asType(methodType(String.class, Object.class));
            getTypeHandle = lookup.findVirtual(c, "getType", methodType(Class.class))
                    .asType(methodType(Class.class, Object.class));
        } catch (ClassNotFoundException| NoSuchMethodException e) {
            // pre-Java-14
            isRecordHandle = MethodHandles.empty(methodType(boolean.class, Class.class));
            getRecordComponentsHandle = null;
            getNameHandle = null;
            getTypeHandle = null;
        } catch (IllegalAccessException unexpected) {
            throw new AssertionError(unexpected);
        }
        IS_RECORD_HANDLE = isRecordHandle;
        GET_RECORD_COMPONENTS_HANDLE = getRecordComponentsHandle;
        GET_NAME_HANDLE = getNameHandle;
        GET_TYPE_HANDLE = getTypeHandle;
    }

    public static boolean isRecord(Class<?> aClass) {
        try {
            return (boolean) IS_RECORD_HANDLE.invokeExact(aClass);
        } catch (WrongMethodTypeException e) {
            throw e;
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    public static String[] getRecordComponents(Class<?> aRecord) {
        if (!isRecord(aRecord)) {
            return new String[0];
        }

        try {
            Object[] components = (Object[]) GET_RECORD_COMPONENTS_HANDLE.invokeExact(aRecord);
            String[] names = new String[components.length];
            for (int i = 0; i < components.length; i++) {
                Object component = components[i];
                names[i] = (String) GET_NAME_HANDLE.invokeExact(component);
            }
            return names;
        } catch (WrongMethodTypeException e) {
            throw e;
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
            Object[] components = (Object[]) GET_RECORD_COMPONENTS_HANDLE.invokeExact(aRecord);
            Class<?>[] types = new Class[components.length];
            for (int i = 0; i < components.length; i++) {
                Object component = components[i];
                types[i] = (Class<?>) GET_TYPE_HANDLE.invokeExact(component);
            }
            return types;
        } catch (WrongMethodTypeException e) {
            throw e;
        } catch (Throwable e) {
            return new Class[0];
        }
    }
}
