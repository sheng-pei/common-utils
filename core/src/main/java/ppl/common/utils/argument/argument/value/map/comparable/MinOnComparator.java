package ppl.common.utils.argument.argument.value.map.comparable;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;
import ppl.common.utils.string.Strings;

import java.util.Comparator;

public class MinOnComparator<V> extends NullPassedPredicateMapper<V> {

    private final V min;
    private final Comparator<V> comparator;

    public MinOnComparator(V min, Comparator<V> comparator) {
        this.min = min;
        this.comparator = comparator;
    }

    @Override
    public boolean test(V v) {
        return comparator.compare(min, v) <= 0;
    }

    @Override
    protected String message() {
        return Strings.format("The value is less than '{}'.", min);
    }
}
