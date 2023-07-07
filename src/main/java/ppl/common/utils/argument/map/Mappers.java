package ppl.common.utils.argument.map;

import ppl.common.utils.argument.map.comparable.Max;
import ppl.common.utils.argument.map.comparable.MaxOnComparator;
import ppl.common.utils.argument.map.comparable.Min;
import ppl.common.utils.argument.map.comparable.MinOnComparator;
import ppl.common.utils.argument.map.len.Length;
import ppl.common.utils.argument.map.len.MaxLength;
import ppl.common.utils.argument.map.len.MinLength;

import java.util.Comparator;
import java.util.function.Function;

public class Mappers {
    @SuppressWarnings("rawtypes")
    private static final Function EMPTY = v -> {throw new MapperException("No value is required.");};

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
}
