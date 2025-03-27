package ppl.common.utils.argument.argument.value.map.comparable;

import ppl.common.utils.argument.argument.value.map.NullPassedPredicateMapper;
import ppl.common.utils.string.Strings;

public class Max<V extends Comparable<V>> extends NullPassedPredicateMapper<V> {

    private final Comparable<V> max;

    public Max(V max) {
        this.max = max;
    }

    @Override
    public boolean test(V v) {
        return max.compareTo(v) >= 0;
    }

    @Override
    protected String message() {
        return Strings.format("The value is more than '{}'.", max);
    }
}

