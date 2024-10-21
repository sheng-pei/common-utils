package ppl.common.utils.ext;

import ppl.common.utils.enumerate.EnumEncoder;

public enum ExtPosition {
    LEFT('l'),
    RIGHT('r');

    private final char flag;

    ExtPosition(char flag) {
        this.flag = flag;
    }

    @EnumEncoder
    public char getFlag() {
        return flag;
    }
}
