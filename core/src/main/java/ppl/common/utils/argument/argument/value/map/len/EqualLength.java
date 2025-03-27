package ppl.common.utils.argument.argument.value.map.len;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;

public class EqualLength<V> extends NullPassedPredicateMapper<V> {
    private final Length<V> length;
    private final int tLength;

    public EqualLength(Length<V> length, int tLength) {
        if (tLength < 0) {
            throw new IllegalArgumentException("Length must be great or equal than zero.");
        }

        this.length = length;
        this.tLength = tLength;
    }

    @Override
    public boolean test(V v) {
        return length.len(v) == tLength;
    }

    @Override
    protected String message() {
        return String.format("The length of the value is not equal to '%s'.", tLength);
    }
}
