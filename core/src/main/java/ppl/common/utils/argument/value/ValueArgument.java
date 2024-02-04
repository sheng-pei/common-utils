package ppl.common.utils.argument.value;

import ppl.common.utils.argument.Argument;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class ValueArgument<K, V> extends Argument<K, V> {
    private final Function<String, Stream<String>> splitter;
    @SuppressWarnings("rawtypes")
    private final List mappers;
    @SuppressWarnings("rawtypes")
    private final Collector collector;

    protected ValueArgument(K name,
                            Function<String, Stream<String>> splitter,
                            @SuppressWarnings("rawtypes") List mappers,
                            @SuppressWarnings("rawtypes") Collector collector,
                            BiFunction<? extends ValueArgument<K, V>, V, String> toCanonicalString) {
        super(name, toCanonicalString);
        this.splitter = splitter;
        this.mappers = mappers == null ? Collections.emptyList() : mappers;
        this.collector = collector;
    }

    public FeedingStream<K, V> stream() {
        return new FeedingStream<K, V>() {

            private Object container;

            @Override
            public void feed(String string) {
                Stream<?> stream = string == null ? Stream.empty() : Stream.of(string);
                if (string != null && splitter != null) {
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
            public ArgumentValue<K, V> produce() {
                if (container == null) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                Function<Object, V> func = (Function<Object, V>) collector.finisher();
                return ArgumentValue.create(ValueArgument.this, func.apply(container));
            }
        };
    }

    public V merge(V v1, V v2) {
        @SuppressWarnings("unchecked")
        V v = (V) collector.combiner().apply(v1, v2);
        return v;
    }

}
