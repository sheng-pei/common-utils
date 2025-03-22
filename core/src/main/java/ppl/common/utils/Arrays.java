package ppl.common.utils;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;
import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.Types;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.*;

public final class Arrays {
    private Arrays() {}

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
        if (Types.isPrimitive(clazz)) {
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

    private static final byte[] ZERO_BYTE_ARRAY = new byte[0];
    private static final short[] ZERO_SHORT_ARRAY = new short[0];
    private static final int[] ZERO_INT_ARRAY = new int[0];
    private static final long[] ZERO_LONG_ARRAY = new long[0];
    private static final boolean[] ZERO_BOOL_ARRAY = new boolean[0];
    private static final char[] ZERO_CHAR_ARRAY = new char[0];
    private static final float[] ZERO_FLOAT_ARRAY = new float[0];
    private static final double[] ZERO_DOUBLE_ARRAY = new double[0];

    public static Object[] zero() {
        return zero(Object.class);
    }

    public static String[] zeroString() {
        return zero(String.class);
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

    public static void fill(long[] a, long val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(long[] a, int fromIndex, int toIndex, long val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(int[] a, int val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(int[] a, int fromIndex, int toIndex, int val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(short[] a, short val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(short[] a, int fromIndex, int toIndex, short val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(char[] a, char val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(char[] a, int fromIndex, int toIndex, char val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(byte[] a, byte val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(boolean[] a, boolean val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(boolean[] a, int fromIndex, int toIndex,
                            boolean val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(double[] a, double val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(double[] a, int fromIndex, int toIndex,double val){
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(float[] a, float val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(float[] a, int fromIndex, int toIndex, float val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static void fill(Object[] a, Object val) {
        java.util.Arrays.fill(a, val);
    }

    public static void fill(Object[] a, int fromIndex, int toIndex, Object val) {
        java.util.Arrays.fill(a, fromIndex, toIndex, val);
    }

    public static <T> Stream<T> stream(T[] array) {
        return java.util.Arrays.stream(array);
    }

    public static <T> Stream<T> stream(T[] array, int startInclusive, int endExclusive) {
        return java.util.Arrays.stream(array, startInclusive, endExclusive);
    }

    public static IntStream stream(int[] array) {
        return java.util.Arrays.stream(array);
    }

    public static IntStream stream(int[] array, int startInclusive, int endExclusive) {
        return java.util.Arrays.stream(array, startInclusive, endExclusive);
    }

    public static LongStream stream(long[] array) {
        return java.util.Arrays.stream(array);
    }

    public static LongStream stream(long[] array, int startInclusive, int endExclusive) {
        return java.util.Arrays.stream(array, startInclusive, endExclusive);
    }

    public static DoubleStream stream(double[] array) {
        return java.util.Arrays.stream(array);
    }

    public static DoubleStream stream(double[] array, int startInclusive, int endExclusive) {
        return java.util.Arrays.stream(array, startInclusive, endExclusive);
    }

    @SafeVarargs
    public static <T> List<T> asList(T... ts) {
        return java.util.Arrays.asList(ts);
    }

    public static String toString(long[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(int[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(short[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(char[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(byte[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(boolean[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(float[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(double[] a) {
        return java.util.Arrays.toString(a);
    }

    public static String toString(Object[] a) {
        return java.util.Arrays.toString(a);
    }

    public static <T> T[] copyOf(T[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        return java.util.Arrays.copyOf(original, newLength, newType);
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static short[] copyOf(short[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static int[] copyOf(int[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static long[] copyOf(long[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static char[] copyOf(char[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static float[] copyOf(float[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static double[] copyOf(double[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static boolean[] copyOf(boolean[] original, int newLength) {
        return java.util.Arrays.copyOf(original, newLength);
    }

    public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
        return java.util.Arrays.copyOfRange(original, from, to, newType);
    }

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static short[] copyOfRange(short[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static int[] copyOfRange(int[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static long[] copyOfRange(long[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static char[] copyOfRange(char[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static float[] copyOfRange(float[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static double[] copyOfRange(double[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }

    public static boolean[] copyOfRange(boolean[] original, int from, int to) {
        return java.util.Arrays.copyOfRange(original, from, to);
    }
}
