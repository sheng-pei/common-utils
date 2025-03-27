package ppl.common.utils.argument.argument.value.map.len;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;

public class MinLength<V> extends NullPassedPredicateMapper<V> {
    private final Length<V> length;
    private final int min;

    public MinLength(Length<V> length, int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Min must be great or equal than zero.");
        }

        this.length = length;
        this.min = min;
    }

    @Override
    protected String message() {
        return String.format("The length of the value is less than '%s'.", min);
    }

    @Override
    public boolean test(V v) {
        return length.len(v) >= min;
    }
}
