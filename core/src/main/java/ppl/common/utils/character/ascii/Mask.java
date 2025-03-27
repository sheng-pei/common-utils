package ppl.common.utils.character.ascii;

import java.util.Objects;

public final class Mask {
    private static final String[] NORMAL_REPRESENTATION = {
            "\\00",   "\\01",  "\\02",  "\\03",  "\\04",  "\\05",  "\\06",  "\\07",
            "\\010",  "\\t",   "\\n",   "\\013", "\\014", "\\r",   "\\016", "\\017",
            "\\020",  "\\021", "\\022", "\\023", "\\024", "\\025", "\\026", "\\027",
            "\\030",  "\\031", "\\032", "\\033", "\\034", "\\035", "\\036", "\\037",
            " ",      "!",     "\"",    "#",     "$",     "%",     "&",     "'",
            "(",      ")",     "*",     "+",     ",",     "\\-",   ".",     "/",
            "0",      "1",     "2",     "3",     "4",     "5",     "6",     "7",
            "8",      "9",     ":",     ";",     "<",     "=",     ">",     "?",
            "@",      "A",     "B",     "C",     "D",     "E",     "F",     "G",
            "H",      "I",     "J",     "K",     "L",     "M",     "N",     "O",
            "P",      "Q",     "R",     "S",     "T",     "U",     "V",     "W",
            "X",      "Y",     "Z",     "\\[",   "\\\\",  "\\]",   "^",     "_",
            "`",      "a",     "b",     "c",     "d",     "e",     "f",     "g",
            "h",      "i",     "j",     "k",     "l",     "m",     "n",     "o",
            "p",      "q",     "r",     "s",     "t",     "u",     "v",     "w",
            "x",      "y",     "z",     "{",     "|",     "}",     "~",     "\\0177",
    };
    private static final int NON_ASCII_MASK = 3;
    public static final Mask NON_ASCII = new Mask(0L, 0L, (byte) 3);
    public static final Mask NON_OCTET = new Mask(0L, 0L, (byte) 2);
    public static final Mask OCTET = new Mask(~0L, ~0L, (byte) 1);
    private final long low;
    private final long high;
    private final byte nonAscii;

    private Mask(long low, long high) {
        this(low, high, (byte) 0);
    }

    private Mask(long low, long high, byte nonAscii) {
        this.low = low;
        this.high = high;
        this.nonAscii = nonAscii;
    }

    public String patternString() {
        StringBuilder builder = new StringBuilder();
        char start = '\0';
        char next = start;
        for (; next < '\100'; next ++) {
            if ((low & (1L << next)) == 0) {
                builder.append(patternOf(start, next));
                start = (char) (next + 1);
            }
        }
        for (; next < '\200'; next ++) {
            if ((high & (1L << next)) == 0) {
                builder.append(patternOf(start, next));
                start = (char) (next + 1);
            }
        }
        builder.append(patternOf(start, '\200'));
        if ((nonAscii & 0x01) != 0) {
            builder.append("\\0200-\\0377");
        }
        if ((nonAscii & 0x02) != 0) {
            builder.append("\\u0100-\\uffff");
        }
        return builder.toString();
    }

    private static String patternOf(char begin, char end) {
        if (begin + 1 == end) {
            return NORMAL_REPRESENTATION[begin];
        } else if (begin + 2 == end) {
            return NORMAL_REPRESENTATION[begin] + NORMAL_REPRESENTATION[begin + 1];
        } else if (begin < end) {
            char last = (char) (end - 1);
            if (begin >= 'A' && last <= 'Z') {
                return begin + "-" + last;
            }
            if (begin >= 'a' && last <= 'z') {
                return begin + "-" + last;
            }
            if (begin >= '0' && last <= '9') {
                return begin + "-" + last;
            }
            return escape(begin) + "-" + escape(last);
        }
        return "";
    }


    private static String escape(char c) {
        return "\\0" + Integer.toOctalString(c);
    }

    public Mask bitNot() {
        return new Mask(~low, ~high, (byte) (~nonAscii & NON_ASCII_MASK));
    }

    public Mask bitOr(Mask mask) {
        return new Mask(low | mask.low,
                high | mask.high,
                (byte) (nonAscii | mask.nonAscii));
    }

    public Mask bitAnd(Mask mask) {
        return new Mask(low & mask.low,
                high & mask.high,
                (byte) (nonAscii & mask.nonAscii));
    }

    public boolean isSet(char c) {
        if (c < 64) {
            return ((1L << c) & low) != 0;
        }
        if (c < 128) {
            return ((1L << (c - 64)) & high) != 0;
        }
        if (c < 256) {
            return (1 & nonAscii) != 0;
        }
        return (1 << 1 & nonAscii) != 0;
    }

    public MaskCharPredicate predicate() {
        return () -> Mask.this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high, nonAscii);
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
                Objects.equals(high, mask.high) &&
                Objects.equals(nonAscii, mask.nonAscii);
    }

    public static Mask asciiMask(char begin, char end) {
        if (begin > end || end > '\177') {
            throw new IllegalArgumentException("Illegal ascii range.");
        }
        return new Mask(lowMask(begin, end), highMask(begin, end));
    }

    public static Mask asciiMask(String string) {
        Objects.requireNonNull(string, "Mask string is required.");
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (c > '\177') {
                throw new IllegalArgumentException("Illegal ascii mask string.");
            }
        }
        return new Mask(lowMask(chars), highMask(chars));
    }

    private static long lowMask(char begin, char end) {
        if (begin > 63) {
            return 0L;
        }

        long res = 0L;
        for (int i = begin; i <= Math.min(end, 63); i++) {
            res |= 1L << i;
        }
        return res;
    }

    private static long highMask(char begin, char end) {
        if (end < 64 || begin > 127) {
            return 0L;
        }

        long res = 0L;
        int l = Math.max(begin, 64) - 64;
        int e = Math.min(end, 127) - 64;
        for (int i = l; i <= e; i++) {
            res |= 1L << i;
        }
        return res;
    }

    private static long lowMask(char[] chars) {
        long res = 0L;
        for (char c : chars) {
            if (c < 64) {
                res |= (1L << c);
            }
        }
        return res;
    }

    private static long highMask(char[] chars) {
        long res = 0L;
        for (char c : chars) {
            if (c >= 64 && c < 128) {
                res |= (1L << (c - 64));
            }
        }
        return res;
    }
}
