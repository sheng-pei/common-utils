package ppl.common.utils.ext.parser;

import ppl.common.utils.enumerate.EnumEncoder;

public enum ExtPatternPosition {
    LEFT('l'),
    RIGHT('r');

    private final char flag;

    ExtPatternPosition(char flag) {
        this.flag = flag;
    }

    @EnumEncoder
    public char getFlag() {
        return flag;
    }
}
