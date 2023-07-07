package ppl.common.utils.argument.map.comparable;

import ppl.common.utils.argument.map.MapperException;
import ppl.common.utils.string.Strings;

import java.util.function.Function;

public class Min<V extends Comparable<V>> implements Function<V, V> {

    private final Comparable<V> min;

    public Min(V min) {
        this.min = min;
    }

    @Override
    public V apply(V v) {
        if (min.compareTo(v) > 0) {
            throw new MapperException(Strings.format("The value is less than '{}'.", min));
        }
        return v;
    }
}
