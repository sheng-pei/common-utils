package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Collectors {
    @SuppressWarnings("rawtypes")
    private static final DuplicateErrorCollector DUPLICATE_ERROR = new DuplicateErrorCollector();

    @SuppressWarnings("rawtypes")
    private static final FirstPreservedCollector FIRST_PRESERVED = new FirstPreservedCollector();

    @SuppressWarnings("rawtypes")
    private static final ReplaceCollector REPLACE = new ReplaceCollector();

    private static final KVCollector DEFAULT_KV_COLLECTOR = new KVCollector();

    public static <V> Collector<V, V> one() {
        @SuppressWarnings("unchecked")
        Collector<V, V> res = (DuplicateErrorCollector<V>) DUPLICATE_ERROR;
        return res;
    }

    public static <V> Collector<V, V> first() {
        @SuppressWarnings("unchecked")
        Collector<V, V> res = (FirstPreservedCollector<V>) FIRST_PRESERVED;
        return res;
    }

    public static <V> Collector<V, V> last() {
        @SuppressWarnings("unchecked")
        Collector<V, V> res = (ReplaceCollector<V>) REPLACE;
        return res;
    }

    public static <V> Collector<V, List<V>> list() {
        return new ListCollector<>();
    }

    public static <V> Collector<V, Set<V>> set() {
        return new SetCollector<>();
    }

    public static Collector<String, Map<String, String>> kv() {
        return DEFAULT_KV_COLLECTOR;
    }

    public static Collector<String, Map<String, String>> kv(String delim) {
        delim = delim == null ? "" : delim;
        if (delim.isEmpty()) {
            return DEFAULT_KV_COLLECTOR;
        }
        return new KVCollector(delim);
    }
}
