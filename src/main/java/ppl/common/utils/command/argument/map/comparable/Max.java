package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.StringUtils;
import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.MapperException;

public class Max<V extends Comparable<V>> implements Mapper<V, V> {

    private final Comparable<V> max;

    public Max(V max) {
        this.max = max;
    }

    @Override
    public V map(V v) {
        if (max.compareTo(v) < 0) {
            throw new MapperException(StringUtils.format(
                    "The value is more than '{}'.", max));
        }
        return v;
    }
}
