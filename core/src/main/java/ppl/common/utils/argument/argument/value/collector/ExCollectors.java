package ppl.common.utils.argument.argument.value.collector;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ExCollectors {
    public static <V> Collector<V, ?, Optional<V>> optionalLast() {
        return new OptionalCollector<>(Type.LAST_SEEN, false);
    }
    public static <V> Collector<V, ?, Optional<V>> optionalFirst() {
        return new OptionalCollector<>(Type.FIRST_SEEN, false);
    }
    public static <V> Collector<V, ?, Optional<V>> optional() {
        return new OptionalCollector<>(Type.ONLY_ONE, false);
    }
    public static <V> Collector<V, ?, V> required() {
        return new OneCollector<>(Type.ONLY_ONE, true);
    }
    public static <V> Collector<V, ?, V> one() {
        return new OneCollector<>();
    }

    public static <V> Collector<V, ?, V> first() {
        return new OneCollector<>(Type.FIRST_SEEN);
    }

    public static <V> Collector<V, ?, V> last() {
        return new OneCollector<>(Type.LAST_SEEN);
    }

    public static <V> Collector<V, ?, List<V>> list() {
        return Collectors.toList();
    }

    public static <V> Collector<V, ?, Set<V>> set() {
        return Collectors.toSet();
    }

    public static Collector<String, ?, Map<String, String>> kv() {
        return new KVCollector();
    }

    public static Collector<String, ?, Map<String, String>> kv(String separator) {
        separator = separator == null ? "" : separator;
        if (separator.isEmpty()) {
            throw new IllegalArgumentException("Separator is required.");
        }
        return new KVCollector(separator);
    }
}
