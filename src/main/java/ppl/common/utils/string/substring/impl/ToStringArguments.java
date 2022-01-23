package ppl.common.utils.string.substring.impl;

import ppl.common.utils.string.substring.PositionalArguments;

public class ToStringArguments implements PositionalArguments {

    private final Object[] targets;
    private int pos = 0;

    public ToStringArguments(Object... targets) {
        this.targets = targets;
    }

    @Override
    public boolean available() {
        return this.pos < this.targets.length;
    }

    @Override
    public String consume() {
        if (this.pos >= this.targets.length) {
            throw new IllegalStateException("Parameter index out of range.");
        }

        Object res = this.targets[this.pos++];
        return res == null ? "null" : res.toString();
    }

}
