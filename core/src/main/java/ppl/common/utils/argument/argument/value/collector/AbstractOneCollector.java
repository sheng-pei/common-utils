package ppl.common.utils.argument.argument.value.collector;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class AbstractOneCollector<S, T> implements Collector<S, One<S>, T> {
    private final Type type;
    private final boolean required;
    private final Function<S, T> finisher;

//    OneCollector() {
//        this(null, false);
//    }
//
//    OneCollector(Type type) {
//        this(type, false);
//    }
//
//    OneCollector(Type type, boolean required) {
//        this(type, required, Function.identity())
//    }

    AbstractOneCollector(Type type, boolean required, Function<S, T> finisher) {
        if (type == null) {
            type = Type.ONLY_ONE;
        }
        this.type = type;
        this.required = required;
        this.finisher = finisher;
    }

    @Override
    public Supplier<One<S>> supplier() {
        return () -> new One<>(type, required);
    }

    @Override
    public BiConsumer<One<S>, S> accumulator() {
        return One::accumulate;
    }

    @Override
    public BinaryOperator<One<S>> combiner() {
        return One::combine;
    }

    @Override
    public Function<One<S>, T> finisher() {
        return o -> finisher.apply(o.get());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

}
