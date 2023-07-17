package ppl.common.utils.character.ascii;

public enum AsciiGroup implements MaskCharPredicate {
    EMPTY(""),
    NUL("\0"),
    VCHAR('\041', '\176'),
    UP_ALPHA('A', 'Z'),
    LOW_ALPHA('a', 'z'),
    DIGIT('0', '9'),
    ALPHA(UP_ALPHA, LOW_ALPHA),
    ALPHA_NUM(ALPHA, DIGIT),
    UP_HEX_DIGIT(DIGIT, Mask.mask('A', 'F').predicate()),
    LOW_HEX_DIGIT(DIGIT, Mask.mask('a', 'f').predicate()),
    HEX_DIGIT(UP_HEX_DIGIT, LOW_HEX_DIGIT),
    ALL('\000', '\177');

    private final Mask mask;

    AsciiGroup(MaskCharPredicate... predicates) {
        Mask mask = Mask.mask("");
        for (MaskCharPredicate p : predicates) {
            mask = mask.bitOr(p.mask());
        }
        this.mask = mask;
    }

    AsciiGroup(String string) {
        this.mask = Mask.mask(string);
    }

    AsciiGroup(char begin, char end) {
        this.mask = Mask.mask(begin, end);
    }

    @Override
    public Mask mask() {
        return mask;
    }
}
