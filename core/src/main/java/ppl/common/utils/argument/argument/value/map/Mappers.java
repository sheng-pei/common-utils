package ppl.common.utils.argument.argument.value.map;

import ppl.common.utils.argument.argument.value.map.comparable.Max;
import ppl.common.utils.argument.argument.value.map.comparable.MaxOnComparator;
import ppl.common.utils.argument.argument.value.map.comparable.Min;
import ppl.common.utils.argument.argument.value.map.comparable.MinOnComparator;
import ppl.common.utils.argument.argument.value.map.len.Length;
import ppl.common.utils.argument.argument.value.map.len.MaxLength;
import ppl.common.utils.argument.argument.value.map.len.MinLength;
import ppl.common.utils.string.Strings;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Mappers {
    @SuppressWarnings("rawtypes")
    private static final Function EMPTY = v -> {throw new MapperException("No value is needed.");};
    @SuppressWarnings("rawtypes")
    private static final Function REQUIRED = v -> {
        if (v == null) {
            throw new MapperException("The value is required.");
        }
        return v;
    };

    public static <V extends Comparable<V>> Function<V, V> max(V max) {
        return new Max<>(max);
    }

    public static <V> Function<V, V> max(V max, Comparator<V> comparator) {
        return new MaxOnComparator<>(max, comparator);
    }

    public static <V extends Comparable<V>> Function<V, V> min(V min) {
        return new Min<>(min);
    }

    public static <V> Function<V, V> min(V min, Comparator<V> comparator) {
        return new MinOnComparator<>(min, comparator);
    }

    public static <V> Function<V, V> maxLength(Length<V> len, int max) {
        return new MaxLength<V>(len, max);
    }

    public static <V> Function<V, V> minLength(Length<V> len, int min) {
        return new MinLength<V>(len, min);
    }

    public static <V> Function<V, V> noValue() {
        @SuppressWarnings("unchecked")
        Function<V, V> res = (Function<V, V>) EMPTY;
        return res;
    }

    public static <V> Function<V, V> required() {
        @SuppressWarnings("unchecked")
        Function<V, V> res = (Function<V, V>) REQUIRED;
        return res;
    }

    public static <V> Function<V, V> predicate(Predicate<V> predicate) {
        Objects.requireNonNull(predicate);
        return new NullPassedPredicateMapper<V>() {
            @Override
            public boolean test(V v) {
                return predicate.test(v);
            }
        };
    }

    public static <V> Function<V, V> predicate(Predicate<V> predicate, String message) {
        Objects.requireNonNull(predicate);
        if (Strings.isEmpty(message)) {
            throw new IllegalArgumentException("Message is required.");
        }
        return new NullPassedPredicateMapper<V>() {
            @Override
            public boolean test(V v) {
                return predicate.test(v);
            }

            @Override
            protected String message() {
                return message;
            }
        };
    }
}
