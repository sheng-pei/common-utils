package ppl.common.utils.argument.argument.value.map.comparable;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;
import ppl.common.utils.string.Strings;

public class Min<V extends Comparable<V>> extends NullPassedPredicateMapper<V> {

    private final Comparable<V> min;

    public Min(V min) {
        this.min = min;
    }

    @Override
    public boolean test(V v) {
        return min.compareTo(v) <= 0;
    }

    @Override
    protected String message() {
        return Strings.format("The value is less than '{}'.", min);
    }
}
