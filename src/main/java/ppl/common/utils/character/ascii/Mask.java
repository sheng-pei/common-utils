package ppl.common.utils.character.ascii;

import java.util.Objects;

public final class Mask {
    public static final Mask CHARACTER_EXCEPT_NON_NUL_ASCII = new Mask(1L, 0L);
    private final long low;
    private final long high;

    private Mask(long low, long high) {
        this.low = low;
        this.high = high;
    }

    public Mask bitNot() {
        return new Mask(~low, ~high);
    }

    public Mask bitOr(Mask mask) {
        return new Mask(low | mask.low, high | mask.high);
    }

    public Mask bitAnd(Mask mask) {
        return new Mask(low & mask.low, high & mask.high);
    }

    public boolean isSet(char c) {
        if (c == 0 || c >= 128) {
            return (low & 1L) != 0;
        }
        if (c < 64) {
            return ((1L << c) & low) != 0;
        }
        return ((1L << (c - 64)) & high) != 0;
    }

    public MaskCharacterPredicate predicate() {
        return () -> Mask.this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mask)) {
            return false;
        }
        Mask mask = (Mask) obj;
        return Objects.equals(low, mask.low) &&
                Objects.equals(high, mask.high);
    }

    public static Mask mask(char begin, char end) {
        return new Mask(lowMask(begin, end), highMask(begin, end));
    }

    public static Mask mask(String string) {
        return new Mask(lowMask(string), highMask(string));
    }

    private static long lowMask(char begin, char end) {
        if (begin > end) {
            throw new IllegalArgumentException("Illegal range.");
        }

        long res = 0L;
        int l = Math.min(begin, 64);
        int e = Math.min(end, 64);
        for (int i = l; i <= e; i++) {
            if (0 < i && i < 64) {
                res |= 1L << i;
            }
        }
        return res;
    }

    private static long highMask(char begin, char end) {
        if (begin > end) {
            throw new IllegalArgumentException("Illegal range.");
        }

        if (end < 64) {
            return 0L;
        }

        long res = 0L;
        int l = Math.min(Math.max(begin, 64), 128) - 64;
        int e = Math.min(end, 128) - 64;
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
            if (0 < c && c < 64) {
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
}
