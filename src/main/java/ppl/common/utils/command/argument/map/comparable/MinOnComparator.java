package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.StringUtils;
import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

import java.util.Comparator;

public class MinOnComparator<V> implements Mapper<V, V> {

    private final V min;
    private final Comparator<V> comparator;

    public MinOnComparator(V min, Comparator<V> comparator) {
        this.min = min;
        this.comparator = comparator;
    }

    @Override
    public V map(V v) {
        if (comparator.compare(min, v) > 0) {
            throw new MapperException(StringUtils.format("The value is less than '{}'.", min));
        }
        return v;
    }
}
