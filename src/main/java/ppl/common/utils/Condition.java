package ppl.common.utils;

public final class Condition {

    private Condition() { }

    public static boolean in(byte value, byte start, byte end) {
        return start <= value && value <= end;
    }

    public static boolean in(short value, short start, short end) {
        return start <= value && value <= end;
    }

    public static boolean in(int value, int start, int end) {
        return start <= value && value <= end;
    }

    public static boolean in(long value, long start, long end) {
        return start <= value && value <= end;
    }

    public static boolean in(char value, char start, char end) {
        return start <= value && value <= end;
    }

    public static <T extends Comparable<T>> boolean in(T value, T start, T end) {
        return start.compareTo(value) <= 0 && value.compareTo(end) <= 0;
    }

}
