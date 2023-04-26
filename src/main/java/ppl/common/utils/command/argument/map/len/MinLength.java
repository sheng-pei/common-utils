package ppl.common.utils.command.argument.map.len;

import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

public class MinLength<V> implements Mapper<V, V> {
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
    public V map(V v) {
        if (length.len(v) < min) {
            throw new MapperException(String.format(
                    "The length of the value is less than '%s'.", min));
        }
        return v;
    }
}
