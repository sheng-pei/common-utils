package ppl.common.utils.net;

public enum URICharacter implements MaskPredicate {
    EMPTY(""),
    RESERVED(":/?#[]@!$&’()*+,;="),
    UP_ALPHA('A', 'Z'),
    LOW_ALPHA('a', 'z'),
    DIGIT('0', '9'),
    ALPHA(UP_ALPHA, LOW_ALPHA),
    ALPHA_NUM(ALPHA, DIGIT),
    UNRESERVED(ALPHA_NUM.or("-_.~")),
    HEX(DIGIT.or('a', 'f').or('A', 'F'));

    private final Mask mask;

    URICharacter(Mask... masks) {
        Mask mask = USAsciiMatcher.mask("");
        for (Mask m : masks) {
            mask = mask.or(m);
        }
        this.mask = mask;
    }

    URICharacter(String string) {
        this.mask = USAsciiMatcher.mask(string);
    }

    URICharacter(char begin, char end) {
        this.mask = USAsciiMatcher.mask(begin, end);
    }

    @Override
    public long lowMask() {
        return mask.lowMask();
    }

    @Override
    public long highMask() {
        return mask.highMask();
    }

}
