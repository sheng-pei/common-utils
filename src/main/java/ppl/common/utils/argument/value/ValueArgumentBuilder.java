package ppl.common.utils.argument.value;

import ppl.common.utils.argument.value.collector.ExCollectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public abstract class ValueArgumentBuilder<K, V, A extends ValueArgument<K, V>> {

    protected K name;
    protected Function<String, Stream<String>> splitter;
    @SuppressWarnings("rawtypes")
    protected List mappers;
    @SuppressWarnings("rawtypes")
    protected Collector collector;

    public ValueArgumentBuilder(K name) {
        this.name = name;
    }

    public ValueArgumentBuilder<K, V, A> split(Function<String, Stream<String>> splitter) {
        Objects.requireNonNull(splitter);
        this.splitter = splitter;
        return self();
    }

    public ValueArgumentBuilder<K, V, A> map(Function<V, V> mapper) {
        return map(mapper, null);
    }

    public <R, A extends ValueArgument<K, R>> ValueArgumentBuilder<K, R, A> map(Function<V, R> mapper, TypeReference<A> ref) {
        Objects.requireNonNull(mapper);
        if (collector != null) {
            throw new IllegalStateException("Setting mapper after collector is not allowed.");
        }

        @SuppressWarnings("unchecked")
        List<Function<V, R>> mappers = this.mappers;
        if (mappers == null) {
            mappers = new ArrayList<>();
            this.mappers = mappers;
        }
        mappers.add(mapper);
        return self();
    }

    public ValueArgumentBuilder<K, V, A> collect() {
        this.collector = ExCollectors.one();
        return self();
    }

    public ValueArgumentBuilder<K, V, A> collect(Collector<V, ?, V> collector) {
        return collect(collector, null);
    }

    public <R, A extends ValueArgument<K, R>> ValueArgumentBuilder<K, R, A> collect(Collector<V, ?, R> collector, TypeReference<A> ref) {
        Objects.requireNonNull(collector);
        this.collector = collector;
        return self();
    }

    private <S> S self() {
        @SuppressWarnings("unchecked")
        S self = (S) this;
        return self;
    }

    public A build(BiFunction<A, V, String> toCanonicalString) {
        List<?> mappers = this.mappers;
        Collector<?, ?, ?> collector = this.collector;
        return create(name, splitter,
                mappers == null ? null : Collections.unmodifiableList(mappers),
                collector == null ? ExCollectors.one() : collector,
                toCanonicalString);
    }

    protected abstract A create(
            K name,
            Function<String, Stream<String>> splitter,
            List<?> mappers,
            Collector<?, ?, ?> collector,
            BiFunction<A, V, String> toCanonicalString);

}
