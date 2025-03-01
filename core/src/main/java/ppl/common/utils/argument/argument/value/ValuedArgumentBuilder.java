package ppl.common.utils.argument.argument.value;

import ppl.common.utils.argument.argument.Argument;
import ppl.common.utils.argument.argument.ArgumentBuilder;
import ppl.common.utils.argument.argument.value.collector.ExCollectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public abstract class ValuedArgumentBuilder<V> extends ArgumentBuilder {

    protected Function<String, Stream<String>> splitter;
    @SuppressWarnings("rawtypes")
    protected List mappers;
    @SuppressWarnings("rawtypes")
    protected Collector collector;

    public ValuedArgumentBuilder(String name) {
        super(name);
    }

    public ValuedArgumentBuilder<V> split(Function<String, Stream<String>> splitter) {
        Objects.requireNonNull(splitter);
        this.splitter = splitter;
        return self();
    }

    public <R> ValuedArgumentBuilder<R> map(Function<V, R> mapper) {
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

    public ValuedArgumentBuilder<V> collect() {
        this.collector = ExCollectors.one();
        return self();
    }

    public <R> ValuedArgumentBuilder<R> collect(Collector<V, ?, R> collector) {
        Objects.requireNonNull(collector);
        this.collector = collector;
        return self();
    }

    private <S> S self() {
        @SuppressWarnings("unchecked")
        S self = (S) this;
        return self;
    }

    public final <A extends ValuedArgument<V>> A build(Function<V, String> normalizer) {
        List<?> mappers = this.mappers;
        Collector<?, ?, ?> collector = this.collector;
        return create(name, splitter,
                mappers == null ? null : Collections.unmodifiableList(mappers),
                collector == null ? ExCollectors.one() : collector, normalizer);
    }

    @Override
    protected final <A extends Argument> A create(String name) {
        @SuppressWarnings("unchecked")
        A ret = (A) create(name, splitter, mappers, collector, Object::toString);
        return ret;
    }

    protected abstract <A extends ValuedArgument<V>> A create(
            String name,
            Function<String, Stream<String>> splitter,
            List<?> mappers,
            Collector<?, ?, ?> collector,
            Function<V, String> valueNormalizer);

}
