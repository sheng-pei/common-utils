package ppl.common.utils.argument.argument.value.map.len;

import ppl.common.utils.argument.argument.value.map.MapperException;

import java.util.function.Function;

public class EqualLength<V> implements Function<V, V> {
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
    public V apply(V v) {
        if (length.len(v) != tLength) {
            throw new MapperException(String.format(
                    "The length of the value is not equal to '%s'.", tLength));
        }
        return v;
    }
}
