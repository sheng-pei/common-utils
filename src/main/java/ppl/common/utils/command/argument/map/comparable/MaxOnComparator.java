package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.string.Strings;
import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

import java.util.Comparator;

public class MaxOnComparator<V> implements Mapper<V, V> {

    private final V max;
    private final Comparator<V> comparator;

    public MaxOnComparator(V max, Comparator<V> comparator) {
        this.max = max;
        this.comparator = comparator;
    }

    @Override
    public V map(V v) {
        if (comparator.compare(max, v) < 0) {
            throw new MapperException(Strings.format("The value is more than '{}'.", max));
        }
        return v;
    }
}
