package ppl.common.utils.ext;

import ppl.common.utils.enumerate.EnumEncoder;

import java.util.function.Supplier;

public enum ExtKind {
    EQUALS('e', EqualsExtSelector::new),
    PREFIX('p', PrefixExtSelector::new);

    private final char flag;
    private final Supplier<ExtSelector> supplier;

    ExtKind(char flag, Supplier<ExtSelector> supplier) {
        this.flag = flag;
        this.supplier = supplier;
    }

    @EnumEncoder
    public char getFlag() {
        return flag;
    }

    public ExtSelector create() {
        return supplier.get();
    }
}
