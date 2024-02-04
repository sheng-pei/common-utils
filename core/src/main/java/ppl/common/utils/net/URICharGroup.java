package ppl.common.utils.net;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.character.ascii.MaskCharPredicate;

public enum URICharGroup implements MaskCharPredicate {
    RESERVED(":/?#[]@!$&’()*+,;="),
    UNRESERVED(AsciiGroup.ALPHA_NUM, Mask.mask("-_.~").predicate());

    private final Mask mask;

    URICharGroup(MaskCharPredicate... predicates) {
        Mask mask = Mask.mask("");
        for (MaskCharPredicate p : predicates) {
            mask = mask.bitOr(p.mask());
        }
        this.mask = mask;
    }

    URICharGroup(String string) {
        this.mask = Mask.mask(string);
    }

    URICharGroup(char begin, char end) {
        this.mask = Mask.mask(begin, end);
    }

    @Override
    public Mask mask() {
        return mask;
    }
}
