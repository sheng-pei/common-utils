package ppl.common.utils.argument.collector;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class OneCollector<T> implements Collector<T, OneCollector.One<T>, T> {
    private final Type type;

    OneCollector() {
        this(null);
    }

    OneCollector(Type type) {
        if (type == null) {
            type = Type.ONLY_ONE;
        }
        this.type = type;
    }

    @Override
    public Supplier<One<T>> supplier() {
        return () -> new One<>(type);
    }

    @Override
    public BiConsumer<One<T>, T> accumulator() {
        return One::accumulate;
    }

    @Override
    public BinaryOperator<One<T>> combiner() {
        return One::combine;
    }

    @Override
    public Function<One<T>, T> finisher() {
        return One::get;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    static class One<T> {

        private final Type type;
        private T t;

        public One(Type type) {
            this.type = type;
        }

        public T get() {
            return t;
        }

        public void accumulate(T t) {
            switch (type) {
                case ONLY_ONE:
                    if (this.t != null) {
                        throw new CollectorException("Only one is allowed in the stream.");
                    }
                    this.t = t;
                    break;
                case LAST_SEEN:
                    this.t = t;
                    break;
                case FIRST_SEEN:
                    if (this.t == null) {
                        this.t = t;
                    }
            }
        }

        public One<T> combine(One<T> one) {
            T other = one.get();
            if (other != null) {
                this.accumulate(other);
            }
            return this;
        }
    }

    public enum Type {
        FIRST_SEEN, LAST_SEEN, ONLY_ONE;
    }
}
