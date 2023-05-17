package ppl.common.utils.net;

import java.util.function.Predicate;

class First128Matcher {

    static long lowMask(char begin, char end) {
        long res = 0L;
        int l = Math.max(Math.min(begin, 63), 1);
        int e = Math.max(Math.min(end, 63), 1);
        for (int i = l; i <= e; i++) {
            res |= 1L << i;
        }
        return res;
    }

    static long highMask(char begin, char end) {
        long res = 0L;
        int l = Math.min(Math.max(begin, 64), 127);
        int e = Math.min(Math.max(end, 64), 127);
        for (int i = l; i <= e; i++) {
            res |= (1L << (i - 64));
        }
        return res;
    }

    static long lowMask(String string) {
        long res = 0L;
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (c > 0 && c < 64) {
                res |= (1L << c);
            }
        }
        return res;
    }

    static long highMask(String string) {
        long res = 0L;
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (c >= 64 && c < 128) {
                res |= (1L << (c - 64));
            }
        }
        return res;
    }

    static boolean match(char c, long lowMask, long highMask) {
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

    interface Mask {
        long lowMask();
        long highMask();

        default IMaskPredicate or(String string) {
            long l = lowMask() | First128Matcher.lowMask(string);
            long h = highMask() | First128Matcher.highMask(string);
            return new IMaskPredicate() {
                @Override
                public boolean test(Character character) {
                    return match(character, l, h);
                }

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

        default IMaskPredicate or(char first, char end) {
            long l = lowMask() | First128Matcher.lowMask(first, end);
            long h = highMask() | First128Matcher.highMask(first, end);
            return new IMaskPredicate() {
                @Override
                public boolean test(Character character) {
                    return match(character, l, h);
                }

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

    interface IMaskPredicate extends Predicate<Character>, Mask {
        @Override
        default Predicate<Character> or(Predicate<? super Character> other) {
            if (other instanceof Mask) {
                Mask o = (Mask) other;
                long l = lowMask() | o.lowMask();
                long h = highMask() | o.highMask();
                return new IMaskPredicate() {
                    @Override
                    public boolean test(Character character) {
                        return match(character, l, h);
                    }

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
            return Predicate.super.or(other);
        }
    }

}
