package ppl.common.utils.command.argument.map.len;

import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

public class EqualLength<V> implements Mapper<V, V> {
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
    public V map(V v) {
        if (length.len(v) != tLength) {
            throw new MapperException(String.format(
                    "The length of the value is not equal to '%s'.", tLength));
        }
        return v;
    }
}
