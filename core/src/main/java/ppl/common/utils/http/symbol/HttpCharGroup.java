package ppl.common.utils.http.symbol;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.character.ascii.MaskCharPredicate;

public enum HttpCharGroup implements MaskCharPredicate {

    NON_OCTET(Mask.NON_OCTET.predicate()),
    OCTET(Mask.OCTET.predicate()),
    CHAR(AsciiGroup.ALL),
    //RFC 7230
    OBS_TEXT(OCTET.mask().bitAnd(CHAR.mask().bitNot()).predicate()),
    CONTROL(Mask.asciiMask('\000', '\037').predicate(),
            Mask.asciiMask("\177").predicate()),
    VCHAR(AsciiGroup.VCHAR),
    //RFC 7230
    FIELD_VCHAR(AsciiGroup.VCHAR, OBS_TEXT),
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
    //RFC 7230
    WHITESPACE(SP, HT),
    //RFC 7230
    TCHAR(AsciiGroup.ALPHA_NUM,
            Mask.asciiMask("!#$%&'*+-.^_`|~").predicate()),
    L_COMMENT("("),
    R_COMMENT(")"),
    DQUOTE("\""),
    //RFC 7230
    QDPTEXT(SP, HT, VCHAR, OBS_TEXT),
    //RFC 7230
    CTEXT(QDPTEXT.mask.bitAnd(Mask.asciiMask("()\\").bitNot()).predicate()),
    //RFC 7230
    QDTEXT(QDPTEXT.mask.bitAnd(Mask.asciiMask("\\\"").bitNot()).predicate()),
    //RFC 2046
    BCHARS_NO_SPACE(DIGIT, ALPHA, Mask.asciiMask("'()+_,-./:=?").predicate()),
    //RFC 2046
    BCHARS(BCHARS_NO_SPACE, SP),
    SEPARATORS(WHITESPACE, DQUOTE, Mask.asciiMask("(),/:;<=>?@[\\]{}").predicate());

    private final Mask mask;

    HttpCharGroup(MaskCharPredicate... predicates) {
        Mask mask = Mask.asciiMask("");
        for (MaskCharPredicate p : predicates) {
            mask = mask.bitOr(p.mask());
        }
        this.mask = mask;
    }

    HttpCharGroup(String string)  {
        this.mask = Mask.asciiMask(string);
    }

    @Override
    public Mask mask() {
        return this.mask;
    }
}
