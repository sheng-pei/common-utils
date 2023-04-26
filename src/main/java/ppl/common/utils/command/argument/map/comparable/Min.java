package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.string.Strings;
import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

public class Min<V extends Comparable<V>> implements Mapper<V, V> {

    private final Comparable<V> min;

    public Min(V min) {
        this.min = min;
    }

    @Override
    public V map(V v) {
        if (min.compareTo(v) > 0) {
            throw new MapperException(Strings.format("The value is less than '{}'.", min));
        }
        return v;
    }
}
