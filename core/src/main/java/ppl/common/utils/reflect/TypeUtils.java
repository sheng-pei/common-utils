package ppl.common.utils.reflect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TypeUtils {

    private static final Map<Class<?>, Class<?>> primitiveToWrapper;
    private static final Map<Class<?>, Class<?>> wrapperToPrimitive;

    private TypeUtils() {}

    static {
        Map<Class<?>, Class<?>> pToW = new HashMap<>();
        pToW.put(byte.class, Byte.class);
        pToW.put(short.class, Short.class);
        pToW.put(int.class, Integer.class);
        pToW.put(long.class, Long.class);
        pToW.put(char.class, Character.class);
        pToW.put(boolean.class, Boolean.class);
        pToW.put(float.class, Float.class);
        pToW.put(double.class, Double.class);
        pToW.put(void.class, Void.class);
        primitiveToWrapper = Collections.unmodifiableMap(pToW);
        Map<Class<?>, Class<?>> wToP = new HashMap<>();
        for (Map.Entry<Class<?>, Class<?>> entry : pToW.entrySet()) {
            wToP.put(entry.getValue(), entry.getKey());
        }
        wrapperToPrimitive = Collections.unmodifiableMap(wToP);
    }

    public static Class<?> box(Class<?> clazz) {
        if (clazz == null || !clazz.isPrimitive()) {
            return clazz;
        }
        return primitiveToWrapper.get(clazz);
    }

    public static Class<?> unbox(Class<?> clazz) {
        if (clazz == null || !wrapperToPrimitive.containsKey(clazz)) {
            return clazz;
        }
        return wrapperToPrimitive.get(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return primitiveToWrapper.containsKey(clazz);
    }

    public static boolean isWrapper(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return wrapperToPrimitive.containsKey(clazz);
    }

}
