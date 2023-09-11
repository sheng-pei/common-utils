package ppl.common.utils.http.symbol;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.character.ascii.MaskCharPredicate;

public enum HttpCharGroup implements MaskCharPredicate {

    OCTET(Mask.OCTET.predicate()),
    CHAR(AsciiGroup.ALL),
    VCHAR(AsciiGroup.VCHAR),
    OBS_TEXT(OCTET.mask().bitAnd(CHAR.mask().bitNot()).predicate()),
    UPALPHA(AsciiGroup.UP_ALPHA),
    LOALPHA(AsciiGroup.LOW_ALPHA),
    ALPHA(AsciiGroup.ALPHA),
    DIGIT(AsciiGroup.DIGIT),
    HEX_DIGIT(AsciiGroup.HEX_DIGIT),
    CR("\r"),
    LF("\n"),
    SP(" "),
    HT("\t"),
    BS("\\"),
    WS(SP, HT),
    CTL(Mask.mask('\000', '\037').predicate(),
            Mask.mask("\177").predicate()),
    NON_CTL_OCTET(OCTET.mask().bitAnd(CTL.mask().bitNot()).predicate()),
    TOKEN(AsciiGroup.ALPHA_NUM,
            Mask.mask("!#$%&'*+-.^_`|~").predicate()),
    SEPARATORS(SP, HT, Mask.mask("()<>@,;:\\\"/[]?={}").predicate()),
    L_COMMENT("("),
    R_COMMENT(")"),
    CTEXT(SP, HT, OBS_TEXT,
            VCHAR.mask.bitAnd(Mask.mask("()\\").bitNot()).predicate()),
    QM("\""),
    QDTEXT(SP, HT, OBS_TEXT,
            VCHAR.mask.bitAnd(Mask.mask("\\\"").bitNot()).predicate()),
    QUOTED_TEXT(SP, HT, VCHAR, OBS_TEXT),
    CL_TOKEN(AsciiGroup.ALPHA_NUM,
             Mask.mask("-").predicate());

    private final Mask mask;

    HttpCharGroup(MaskCharPredicate... predicates) {
        Mask mask = Mask.mask("");
        for (MaskCharPredicate p : predicates) {
            mask = mask.bitOr(p.mask());
        }
        this.mask = mask;
    }

    HttpCharGroup(String string)  {
        this.mask = Mask.mask(string);
    }

    @Override
    public Mask mask() {
        return this.mask;
    }
}
