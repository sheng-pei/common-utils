package ppl.common.utils.argument.argument.value.map.comparable;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;
import ppl.common.utils.string.Strings;

import java.util.Comparator;

public class MaxOnComparator<V> extends NullPassedPredicateMapper<V> {

    private final V max;
    private final Comparator<V> comparator;

    public MaxOnComparator(V max, Comparator<V> comparator) {
        this.max = max;
        this.comparator = comparator;
    }

    @Override
    public boolean test(V v) {
        return comparator.compare(max, v) >= 0;
    }

    @Override
    protected String message() {
        return Strings.format("The value is more than '{}'.", max);
    }
}
