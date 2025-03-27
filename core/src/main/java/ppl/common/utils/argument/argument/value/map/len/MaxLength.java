package ppl.common.utils.argument.argument.value.map.len;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;

public class MaxLength<V> extends NullPassedPredicateMapper<V> {
    private final Length<V> length;
    private final int max;

    public MaxLength(Length<V> length, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Max must be great or equal than zero.");
        }
        this.length = length;
        this.max = max;
    }

    @Override
    public boolean test(V v) {
        return length.len(v) <= max;
    }

    @Override
    protected String message() {
        return String.format("The length of the value is more than '%s'.", max);
    }
}
