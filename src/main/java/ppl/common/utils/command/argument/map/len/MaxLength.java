package ppl.common.utils.command.argument.map.len;

import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

public class MaxLength<V> implements Mapper<V, V> {
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
    public V map(V v) {
        if (length.len(v) > max) {
            throw new MapperException(String.format(
                    "The length of the value is more than '%s'.", max));
        }
        return v;
    }
}
