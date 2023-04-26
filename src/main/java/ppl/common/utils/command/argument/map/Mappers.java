package ppl.common.utils.command.argument.map;

import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.map.comparable.Max;
import ppl.common.utils.command.argument.map.comparable.MaxOnComparator;
import ppl.common.utils.command.argument.map.comparable.Min;
import ppl.common.utils.command.argument.map.comparable.MinOnComparator;
import ppl.common.utils.command.argument.map.len.Length;
import ppl.common.utils.command.argument.map.len.MaxLength;
import ppl.common.utils.command.argument.map.len.MinLength;

import java.util.Comparator;

public class Mappers {

    @SuppressWarnings("rawtypes")
    private static final Required REQUIRED = new Required();

    public static <V extends Comparable<V>> Mapper<V, V> max(V max) {
        return new Max<>(max);
    }

    public static <V> Mapper<V, V> max(V max, Comparator<V> comparator) {
        return new MaxOnComparator<>(max, comparator);
    }

    public static <V extends Comparable<V>> Mapper<V, V> min(V min) {
        return new Min<>(min);
    }

    public static <V> Mapper<V, V> min(V min, Comparator<V> comparator) {
        return new MinOnComparator<>(min, comparator);
    }

    public static <V> Mapper<V, V> maxLength(Length<V> len, int max) {
        return new MaxLength<V>(len, max);
    }

    public static <V> Mapper<V, V> minLength(Length<V> len, int min) {
        return new MinLength<V>(len, min);
    }

    public static <V> Mapper<V, V> required() {
        @SuppressWarnings("unchecked")
        Mapper<V, V> res = REQUIRED;
        return res;
    }
}
