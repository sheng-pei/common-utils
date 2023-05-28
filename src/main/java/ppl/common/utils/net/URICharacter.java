package ppl.common.utils.net;

import ppl.common.utils.character.ascii.AsciiPredicates;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.character.ascii.MaskCharacterPredicate;

public enum URICharacter implements MaskCharacterPredicate {
    RESERVED(":/?#[]@!$&’()*+,;="),
    UNRESERVED(AsciiPredicates.ALPHA_NUM, Mask.mask("-_.~").predicate());

    private final Mask mask;

    URICharacter(MaskCharacterPredicate... predicates) {
        Mask mask = Mask.mask("");
        for (MaskCharacterPredicate p : predicates) {
            mask = mask.bitOr(p.mask());
        }
        this.mask = mask;
    }

    URICharacter(String string) {
        this.mask = Mask.mask(string);
    }

    URICharacter(char begin, char end) {
        this.mask = Mask.mask(begin, end);
    }

    @Override
    public Mask mask() {
        return mask;
    }
}
