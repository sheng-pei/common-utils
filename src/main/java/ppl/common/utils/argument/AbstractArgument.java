package ppl.common.utils.argument;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class AbstractArgument<K, V> {
    private final K name;
    private final BiFunction<AbstractArgument<K, V>, V, String> toCanonicalString;
    private final Function<String, Stream<String>> splitter;
    @SuppressWarnings("rawtypes")
    private final List mappers;
    @SuppressWarnings("rawtypes")
    private final Collector collector;

    protected AbstractArgument(K name,
                               Function<String, Stream<String>> splitter,
                               @SuppressWarnings("rawtypes") List mappers,
                               @SuppressWarnings("rawtypes") Collector collector,
                               BiFunction<? extends AbstractArgument<K, V>, V, String> toCanonicalString) {
        if (name == null) {
            throw new IllegalArgumentException("Argument name is required.");
        }

        this.name = name;
        this.splitter = splitter;
        this.mappers = mappers == null ? Collections.emptyList() : mappers;
        this.collector = collector;
        @SuppressWarnings("unchecked")
        BiFunction<AbstractArgument<K, V>, V, String> tmp =
                (BiFunction<AbstractArgument<K, V>, V, String>) toCanonicalString;
        this.toCanonicalString = tmp;
    }

    public K getName() {
        return this.name;
    }

    public String toCanonicalString(V value) {
        if (toCanonicalString == null) {
            return name + (value == null ? "" : "=" + value);
        }

        return toCanonicalString.apply(this, value);
    }

    public FeedingStream<V> stream() {
        return new FeedingStream<V>() {

            private Object container;

            @Override
            public void feed(String string) {
                if (string == null) {
                    return;
                }

                Stream<?> stream = Stream.of(string);
                if (splitter != null) {
                    stream = splitter.apply(string);
                }

                for (Object mapper : mappers) {
                    @SuppressWarnings({"rawtypes", "unchecked"})
                    Function<Object, Object> func = (Function) mapper;
                    stream = stream.map(func);
                }

                if (container == null) {
                    container = collector.supplier().get();
                }

                @SuppressWarnings("unchecked")
                BiConsumer<Object, Object> accumulator = collector.accumulator();
                stream.forEach(s -> accumulator.accept(container, s));
            }

            @Override
            public V produce() {
                if (container == null) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                Function<Object, V> func = (Function<Object, V>) collector.finisher();
                return func.apply(container);
            }
        };
    }

    public V merge(V v1, V v2) {
        @SuppressWarnings("unchecked")
        V v = (V) collector.combiner().apply(v1, v2);
        return v;
    }

}
