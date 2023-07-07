package ppl.common.utils.argument;

import ppl.common.utils.argument.collector.Collectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class BaseArgument<K, V> implements Argument<K, V> {
    private final K name;
    private final BiFunction<K, V, String> toCanonicalString;
    private final Function<String, Stream<String>> splitter;
    @SuppressWarnings("rawtypes")
    private final List mappers;
    @SuppressWarnings("rawtypes")
    private final Collector collector;

    private BaseArgument(K name,
                         Function<String, Stream<String>> splitter,
                         @SuppressWarnings("rawtypes") List mappers,
                         @SuppressWarnings("rawtypes") Collector collector,
                         Function<V, String> toCanonicalString) {
        this(name, splitter, mappers, collector, (k, v) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(k);
            if (v == null) {
                return builder.toString();
            }
            String vStr = toCanonicalString.apply(v);
            if (vStr.isEmpty()) {
                return builder.toString();
            }
            return builder.append("=")
                    .append(vStr)
                    .toString();
        });
    }

    private BaseArgument(K name,
                         Function<String, Stream<String>> splitter,
                         @SuppressWarnings("rawtypes") List mappers,
                         @SuppressWarnings("rawtypes") Collector collector,
                         BiFunction<K, V, String> toCanonicalString) {
        if (name == null) {
            throw new IllegalArgumentException("Argument name is required.");
        }

        this.name = name;
        this.splitter = splitter;
        this.mappers = mappers == null ? Collections.emptyList() : mappers;
        this.collector = collector;
        this.toCanonicalString = toCanonicalString;
    }

    protected BaseArgument(BaseArgument<K, V> argument) {
        if (argument == null) {
            throw new IllegalArgumentException("Argument is required.");
        }

        this.name = argument.name;
        this.splitter = argument.splitter;
        this.mappers = argument.mappers;
        this.collector = argument.collector;
        this.toCanonicalString = argument.toCanonicalString;
    }

    @Override
    public K getName() {
        return this.name;
    }

    @Override
    public String toCanonicalString(V value) {
        return toCanonicalString.apply(name, value);
    }

    @Override
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

    @Override
    public V merge(V v1, V v2) {
        @SuppressWarnings("unchecked")
        V v = (V) collector.combiner().apply(v1, v2);
        return v;
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof BaseArgument)) {
            return false;
        }
        BaseArgument<?, ?> baseArgument = (BaseArgument<?, ?>) obj;
        return getName().equals(baseArgument.getName());
    }

    @Override
    public Builder<K, V> copy() {
        Builder<K, V> builder = new Builder<>();
        builder.name = name;
        builder.splitter = splitter;
        builder.mappers = mappers;
        builder.collector = collector;
        return builder;
    }

    public static <K> Argument.Builder<K, String> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<K, V> implements Argument.Builder<K, V> {
        private K name;
        private Function<String, Stream<String>> splitter;
        @SuppressWarnings("rawtypes")
        private List mappers;
        @SuppressWarnings("rawtypes")
        private Collector collector;

        @Override
        public Builder<K, V> name(K name) {
            this.name = name;
            return self();
        }

        @Override
        public Builder<K, V> split(Function<String, Stream<String>> splitter) {
            Objects.requireNonNull(splitter);
            this.splitter = splitter;
            return self();
        }

        @Override
        public <R> Builder<K, R> map(Function<V, R> mapper) {
            Objects.requireNonNull(mapper);
            @SuppressWarnings("unchecked")
            List<Function<V, R>> mappers = this.mappers;
            if (mappers == null) {
                mappers = new ArrayList<>();
                this.mappers = mappers;
            }
            mappers.add(mapper);
            return self();
        }

        @Override
        public Builder<K, V> collect() {
            this.collector = Collectors.one();
            return self();
        }

        @Override
        public <A, R> Argument.Builder<K, R> collect(Collector<V, A, R> collector) {
            Objects.requireNonNull(collector);
            this.collector = collector;
            return self();
        }

        @Override
        public Argument<K, V> build(BiFunction<K, V, String> toCanonicalString) {
            return new BaseArgument<K, V>(name, splitter, mappers, collector, toCanonicalString);
        }

        private <R> Builder<K, R> self() {
            @SuppressWarnings("unchecked")
            Builder<K, R> self = (Builder<K, R>) this;
            return self;
        }
    }
}
