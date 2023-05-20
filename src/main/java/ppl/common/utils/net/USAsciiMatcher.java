package ppl.common.utils.net;

public class USAsciiMatcher {

    private static long lowMask(char begin, char end) {
        long res = 0L;
        int l = Math.min(begin, 64);
        int e = Math.min(end, 64);
        for (int i = l; i <= e; i++) {
            if (i < 64) {
                res |= 1L << i;
            }
        }
        return res;
    }

    private static long highMask(char begin, char end) {
        long res = 0L;
        int l = Math.min(Math.max(begin, 64), 128) - 64;
        int e = Math.min(Math.max(end, 64), 128) - 64;
        for (int i = l; i <= e; i++) {
            if (i < 64) {
                res |= 1L << i;
            }
        }
        return res;
    }

    private static long lowMask(String string) {
        long res = 0L;
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (c < 64) {
                res |= (1L << c);
            }
        }
        return res;
    }

    private static long highMask(String string) {
        long res = 0L;
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (c >= 64 && c < 128) {
                res |= (1L << (c - 64));
            }
        }
        return res;
    }

    public static boolean match(char c, long lowMask, long highMask) {
        if (c == 0) {
            return false;
        }
        if (c < 64) {
            return ((1L << c) & lowMask) != 0;
        }
        if (c < 128) {
            return ((1L << (c - 64)) & highMask) != 0;
        }
        return false;
    }

    public static Mask mask(char begin, char end) {
        long l = lowMask(begin, end);
        long h = highMask(begin, end);
        return new Mask() {
            @Override
            public long lowMask() {
                return l;
            }

            @Override
            public long highMask() {
                return h;
            }
        };
    }

    public static Mask mask(String string) {
        long l = lowMask(string);
        long h = highMask(string);
        return new Mask() {
            @Override
            public long lowMask() {
                return l;
            }

            @Override
            public long highMask() {
                return h;
            }
        };
    }

}
