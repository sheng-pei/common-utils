package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.StringUtils;
import ppl.common.utils.command.argument.Mapper;

public class Max<V extends Comparable<V>> extends BaseComparable<V> implements Mapper<V, V> {

    private final Comparable<V> max;

    public Max(V max) {
        this.max = max;
    }

    @Override
    public V map(V v) {
        if (max.compareTo(v) < 0) {
            throw new IllegalArgumentException(StringUtils.format("is more than '{}'.", max));
        }
        return v;
    }
}
