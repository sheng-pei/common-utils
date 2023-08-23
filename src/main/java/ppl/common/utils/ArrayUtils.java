package ppl.common.utils;

public final class ArrayUtils {
    private ArrayUtils() {}

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }
}
