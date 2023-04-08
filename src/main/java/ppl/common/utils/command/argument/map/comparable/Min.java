package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.StringUtils;

public class Min<V> extends BaseComparable<V> implements ppl.common.utils.command.argument.Mapper<V, V> {

    private final Comparable<V> min;

    public Min(V min) {
        this.min = canComparable(min);
    }

    @Override
    public V map(V v) {
        if (min.compareTo(v) > 0) {
            throw new IllegalArgumentException(StringUtils.format("is less than '{}'.", min));
        }
        return v;
    }
}
