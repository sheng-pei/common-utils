package ppl.common.utils.argument.argument.value.map.comparable;

import ppl.common.utils.argument.argument.value.map.MapperException;
import ppl.common.utils.string.Strings;

import java.util.Comparator;
import java.util.function.Function;

public class MinOnComparator<V> implements Function<V, V> {

    private final V min;
    private final Comparator<V> comparator;

    public MinOnComparator(V min, Comparator<V> comparator) {
        this.min = min;
        this.comparator = comparator;
    }

    @Override
    public V apply(V v) {
        if (comparator.compare(min, v) > 0) {
            throw new MapperException(Strings.format("The value is less than '{}'.", min));
        }
        return v;
    }
}
