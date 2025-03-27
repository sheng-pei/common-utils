package ppl.common.utils.argument.argument.value.map;

import ppl.common.utils.string.Strings;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class NullPassedPredicateMapper<V> implements Function<V, V>, Predicate<V> {

    private static final String DEFAULT_MESSAGE = "Predicate test failed.";

    @Override
    public final V apply(V v) {
        if (v == null) {
            return null;
        }
        if (!test(v)) {
            throw new MapperException(Strings.isEmpty(message()) ? DEFAULT_MESSAGE : message());
        }
        return v;
    }

    protected String message() {
        return null;
    }
}
