package ppl.common.utils.argument.collector;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

public class Collectors {
    public static <V> Collector<V, ?, V> one() {
        return new OneCollector<>();
    }

    public static <V> Collector<V, ?, V> first() {
        return new OneCollector<>(OneCollector.Type.FIRST_SEEN);
    }

    public static <V> Collector<V, ?, V> last() {
        return new OneCollector<>(OneCollector.Type.LAST_SEEN);
    }

    public static <V> Collector<V, ?, List<V>> list() {
        return java.util.stream.Collectors.toList();
    }

    public static <V> Collector<V, ?, Set<V>> set() {
        return java.util.stream.Collectors.toSet();
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
