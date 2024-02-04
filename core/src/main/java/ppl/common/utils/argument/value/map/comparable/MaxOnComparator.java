package ppl.common.utils.argument.value.map.comparable;

import ppl.common.utils.argument.value.map.MapperException;
import ppl.common.utils.string.Strings;

import java.util.Comparator;
import java.util.function.Function;

public class MaxOnComparator<V> implements Function<V, V> {

    private final V max;
    private final Comparator<V> comparator;

    public MaxOnComparator(V max, Comparator<V> comparator) {
        this.max = max;
        this.comparator = comparator;
    }

    @Override
    public V apply(V v) {
        if (comparator.compare(max, v) < 0) {
            throw new MapperException(Strings.format("The value is more than '{}'.", max));
        }
        return v;
    }
}
