package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.StringUtils;

import java.util.Comparator;

public class MinOnComparator<V> implements ppl.common.utils.command.argument.Mapper<V, V> {

    private final V min;
    private final Comparator<V> comparator;

    public MinOnComparator(V min, Comparator<V> comparator) {
        this.min = min;
        this.comparator = comparator;
    }

    @Override
    public V map(V v) {
        if (comparator.compare(min, v) > 0) {
            throw new IllegalArgumentException(StringUtils.format("is less than '{}'.", min));
        }
        return v;
    }
}
