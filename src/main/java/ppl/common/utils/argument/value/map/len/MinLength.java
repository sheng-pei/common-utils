package ppl.common.utils.argument.value.map.len;

import ppl.common.utils.argument.value.map.MapperException;

import java.util.function.Function;

public class MinLength<V> implements Function<V, V> {
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
    public V apply(V v) {
        if (length.len(v) < min) {
            throw new MapperException(String.format(
                    "The length of the value is less than '%s'.", min));
        }
        return v;
    }
}
