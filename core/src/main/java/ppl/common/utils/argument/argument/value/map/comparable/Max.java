package ppl.common.utils.argument.argument.value.map.comparable;

import ppl.common.utils.argument.argument.value.map.MapperException;
import ppl.common.utils.string.Strings;

import java.util.function.Function;

public class Max<V extends Comparable<V>> implements Function<V, V> {

    private final Comparable<V> max;

    public Max(V max) {
        this.max = max;
    }

    @Override
    public V apply(V v) {
        if (max.compareTo(v) < 0) {
            throw new MapperException(Strings.format(
                    "The value is more than '{}'.", max));
        }
        return v;
    }
}
