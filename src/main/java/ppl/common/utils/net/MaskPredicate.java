package ppl.common.utils.net;

public enum MaskPredicate implements First128Matcher.IMaskPredicate {
    RESERVED(":/?#[]@!$&’()*+,;="),
    UP_ALPHA('A', 'Z'),
    LOW_ALPHA('a', 'z'),
    DIGIT('0', '9'),
    ALPHA(UP_ALPHA, LOW_ALPHA),
    ALPHA_NUM(ALPHA, DIGIT),
    UNRESERVED(ALPHA_NUM.or("-_.~")),
    HEX(DIGIT.or('a', 'f').or('A', 'F'));

    private final long lowMask;
    private final long highMask;

    MaskPredicate(First128Matcher.Mask... predicates) {
        long lowMask = 0L;
        long highMask = 0L;
        for (First128Matcher.Mask predicate : predicates) {
            lowMask |= predicate.lowMask();
            highMask |= predicate.highMask();
        }
        this.lowMask = lowMask;
        this.highMask = highMask;
    }

    MaskPredicate(String string) {
        this.lowMask = First128Matcher.lowMask(string);
        this.highMask = First128Matcher.highMask(string);
    }

    MaskPredicate(char begin, char end) {
        this.lowMask = First128Matcher.lowMask(begin, end);
        this.highMask = First128Matcher.highMask(begin, end);
    }

    @Override
    public boolean test(Character character) {
        if (character == null) {
            return false;
        }
        return First128Matcher.match(character, lowMask, highMask);
    }

    @Override
    public long lowMask() {
        return lowMask;
    }

    @Override
    public long highMask() {
        return highMask;
    }

}
