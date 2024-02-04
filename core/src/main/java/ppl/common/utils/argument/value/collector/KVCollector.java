package ppl.common.utils.argument.value.collector;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class KVCollector implements Collector<String, KVCollector.KVPairs, Map<String, String>> {
    private final String separator;

    KVCollector() {
        this("=");
    }

    KVCollector(String separator) {
        this.separator = separator;
    }

    @Override
    public Supplier<KVPairs> supplier() {
        return () -> new KVPairs(separator);
    }

    @Override
    public BiConsumer<KVPairs, String> accumulator() {
        return KVPairs::accumulate;
    }

    @Override
    public BinaryOperator<KVPairs> combiner() {
        return KVPairs::combine;
    }

    @Override
    public Function<KVPairs, Map<String, String>> finisher() {
        return KVPairs::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
    }

    static class KVPairs {
        private final String separator;
        private final Map<String, String> kvPairs;

        KVPairs(String separator) {
            this.separator = separator;
            this.kvPairs = new HashMap<>();
        }

        Map<String, String> get() {
            return new HashMap<>(kvPairs);
        }

        void accumulate(String string) {
            int equalIdx = string.indexOf(separator);
            String key = string;
            if (equalIdx >= 0) {
                key = string.substring(0, equalIdx).trim();
            }
            if (key.isEmpty()) {
                throw new CollectorException("Invalid k-v-pair. No key provided.");
            }

            String value = "";
            if (equalIdx >= 0) {
                value = string.substring(equalIdx + 1);
            }

            if (kvPairs.containsKey(key)) {
                throw new CollectorException("Invalid k-v-pair. Duplicate key: " + key);
            }
            kvPairs.put(key, value);
        }

        KVPairs combine(KVPairs kvPairs) {
            Map<String, String> other = kvPairs.get();
            if (other != null) {
                this.kvPairs.putAll(kvPairs.get());
            }
            return this;
        }

    }

}
