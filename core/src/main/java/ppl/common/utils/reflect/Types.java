package ppl.common.utils.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public final class Types {

    private static final Map<Class<?>, Class<?>> primitiveToWrapper;
    private static final Map<Class<?>, Class<?>> wrapperToPrimitive;
    private static final Set<Class<?>> integerClass;
    private static final Set<Class<?>> floatClass;

    private Types() {}

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

        Set<Class<?>> ic = new HashSet<>();
        ic.add(byte.class);
        ic.add(short.class);
        ic.add(int.class);
        ic.add(long.class);
        ic.add(Byte.class);
        ic.add(Short.class);
        ic.add(Integer.class);
        ic.add(Long.class);
        ic.add(BigInteger.class);
        integerClass = ic;

        Set<Class<?>> fc = new HashSet<>();
        fc.add(float.class);
        fc.add(double.class);
        fc.add(Float.class);
        fc.add(Double.class);
        fc.add(BigDecimal.class);
        floatClass = fc;
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

    public static boolean isBaseInteger(Object object) {
        return object != null && isBaseInteger(object.getClass());
    }

    public static boolean isBaseInteger(Class<?> clazz) {
        return Types.isInteger(clazz) && !(BigInteger.class.isAssignableFrom(clazz));
    }

    public static boolean isInteger(Object object) {
        return object != null && isInteger(object.getClass());
    }

    public static boolean isInteger(Class<?> clazz) {
        return integerClass.contains(clazz);
    }

    public static boolean isFloat(Object object) {
        return object != null && isFloat(object.getClass());
    }

    public static boolean isFloat(Class<?> clazz) {
        return floatClass.contains(clazz);
    }

    public static boolean isBoolean(Object object) {
        return object instanceof Boolean;
    }

    public static boolean isBoolean(Class<?> clazz) {
        return clazz == boolean.class || Boolean.class.equals(clazz);
    }

    public static boolean isCharacter(Object object) {
        return object instanceof Character;
    }

    public static boolean isCharacter(Class<?> clazz) {
        return clazz == char.class || Character.class.equals(clazz);
    }

    public static boolean isString(Object object) {
        return object instanceof String;
    }

    public static boolean isString(Class<?> clazz) {
        return String.class.equals(clazz);
    }

}
