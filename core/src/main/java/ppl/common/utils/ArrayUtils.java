package ppl.common.utils;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;
import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.TypeUtils;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public final class ArrayUtils {
    private ArrayUtils() {}

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    private static final Cache<Class<?>, Object> ZERO_CACHE = new ConcurrentReferenceValueCache<>(ReferenceType.WEAK);

    public static <T> T[] zero(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        if (TypeUtils.isPrimitive(clazz)) {
            throw new IllegalArgumentException("Primitive type is not allowed. Use zeroXXX() instead.");
        }

        try {
            @SuppressWarnings("unchecked")
            T[] ret = (T[]) ZERO_CACHE.get(clazz, () -> Array.newInstance(clazz, 0));
            return ret;
        } catch (ExecutionException e) {
            throw new UnreachableCodeException(e);
        }
    }

    private static final Object[] ZERO = new Object[0];
    private static final String[] ZERO_STRING_ARRAY = new String[0];
    private static final byte[] ZERO_BYTE_ARRAY = new byte[0];
    private static final short[] ZERO_SHORT_ARRAY = new short[0];
    private static final int[] ZERO_INT_ARRAY = new int[0];
    private static final long[] ZERO_LONG_ARRAY = new long[0];
    private static final boolean[] ZERO_BOOL_ARRAY = new boolean[0];
    private static final char[] ZERO_CHAR_ARRAY = new char[0];
    private static final float[] ZERO_FLOAT_ARRAY = new float[0];
    private static final double[] ZERO_DOUBLE_ARRAY = new double[0];

    static {
        try {
            ZERO_CACHE.get(Object.class, () -> ZERO);
            ZERO_CACHE.get(String.class, () -> ZERO_STRING_ARRAY);
        } catch (Exception e) {
            //ignore
        }
    }

    public static Object[] zero() {
        return ZERO;
    }

    public static String[] zeroString() {
        return ZERO_STRING_ARRAY;
    }

    public static byte[] zeroByte() {
        return ZERO_BYTE_ARRAY;
    }

    public static short[] zeroShort() {
        return ZERO_SHORT_ARRAY;
    }

    public static int[] zeroInt() {
        return ZERO_INT_ARRAY;
    }

    public static long[] zeroLong() {
        return ZERO_LONG_ARRAY;
    }

    public static boolean[] zeroBool() {
        return ZERO_BOOL_ARRAY;
    }

    public static char[] zeroChar() {
        return ZERO_CHAR_ARRAY;
    }

    public static float[] zeroFloat() {
        return ZERO_FLOAT_ARRAY;
    }

    public static double[] zeroDouble() {
        return ZERO_DOUBLE_ARRAY;
    }
}
