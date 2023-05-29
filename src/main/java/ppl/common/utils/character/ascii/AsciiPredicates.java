package ppl.common.utils.character.ascii;

public enum AsciiPredicates implements MaskCharacterPredicate {
    EMPTY(""),
    UP_ALPHA('A', 'Z'),
    LOW_ALPHA('a', 'z'),
    DIGIT('0', '9'),
    ALPHA(UP_ALPHA, LOW_ALPHA),
    ALPHA_NUM(ALPHA, DIGIT),
    UP_HEX(DIGIT, Mask.mask('A', 'F').predicate()),
    LOW_HEX(DIGIT, Mask.mask('a', 'f').predicate()),
    HEX(UP_HEX, LOW_HEX),
    ALL('\000', '\177');

    private final Mask mask;

    AsciiPredicates(MaskCharacterPredicate... predicates) {
        Mask mask = Mask.mask("");
        for (MaskCharacterPredicate p : predicates) {
            mask = mask.bitOr(p.mask());
        }
        this.mask = mask;
    }

    AsciiPredicates(String string) {
        this.mask = Mask.mask(string);
    }

    AsciiPredicates(char begin, char end) {
        this.mask = Mask.mask(begin, end);
    }

    @Override
    public Mask mask() {
        return mask;
    }
}
