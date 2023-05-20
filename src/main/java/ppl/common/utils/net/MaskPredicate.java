package ppl.common.utils.net;

import java.util.function.Predicate;

public interface MaskPredicate extends Predicate<Character>, Mask {

    @Override
    default boolean test(Character character) {
        if (character == null) {
            return false;
        }
        return USAsciiMatcher.match(character, lowMask(), highMask());
    }

    @Override
    default Predicate<Character> or(Predicate<? super Character> other) {
        if (other instanceof Mask) {
            Mask mask = or((Mask) other);
            return new MaskPredicate() {
                @Override
                public long lowMask() {
                    return mask.lowMask();
                }

                @Override
                public long highMask() {
                    return mask.highMask();
                }
            };
        }
        return Predicate.super.or(other);
    }

    default MaskPredicate or(String string) {
        Mask mask = or(USAsciiMatcher.mask(string));
        return new MaskPredicate() {
            @Override
            public long lowMask() {
                return mask.lowMask();
            }

            @Override
            public long highMask() {
                return mask.highMask();
            }
        };
    }

    default MaskPredicate or(char first, char end) {
        Mask mask = or(USAsciiMatcher.mask(first, end));
        return new MaskPredicate() {
            @Override
            public long lowMask() {
                return mask.lowMask();
            }

            @Override
            public long highMask() {
                return mask.highMask();
            }
        };
    }

    @Override
    default Predicate<Character> and(Predicate<? super Character> other) {
        if (other instanceof Mask) {
            Mask mask = and((Mask) other);
            return new MaskPredicate() {
                @Override
                public long lowMask() {
                    return mask.lowMask();
                }

                @Override
                public long highMask() {
                    return mask.highMask();
                }
            };
        }
        return Predicate.super.and(other);
    }

    @Override
    default Predicate<Character> negate() {
        Mask mask = not();
        return new MaskPredicate() {
            @Override
            public long lowMask() {
                return mask.lowMask();
            }

            @Override
            public long highMask() {
                return mask.highMask();
            }
        };
    }
}
