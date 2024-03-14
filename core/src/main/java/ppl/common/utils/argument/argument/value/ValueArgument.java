package ppl.common.utils.argument.argument.value;

import ppl.common.utils.argument.argument.Argument;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public abstract class ValueArgument<V> extends Argument {
    private final Function<String, Stream<String>> splitter;
    @SuppressWarnings("rawtypes")
    private final List mappers;
    @SuppressWarnings("rawtypes")
    private final Collector collector;
    private final Function<V, String> valueNormalizer;

    protected ValueArgument(String name,
                            Function<String, Stream<String>> splitter,
                            @SuppressWarnings("rawtypes") List mappers,
                            @SuppressWarnings("rawtypes") Collector collector,
                            Function<V, String> valueNormalizer) {
        super(name);
        this.splitter = splitter;
        this.mappers = mappers == null ? Collections.emptyList() : mappers;
        this.collector = collector;
        this.valueNormalizer = valueNormalizer;
    }

    public FeedingStream<V> stream() {
        return new FeedingStream<V>() {

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
            public ArgumentValue<V> produce() {
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

    public String valueString(V v) {
        return valueNormalizer.apply(v);
    }

    @Override
    public abstract String keyString();
}
