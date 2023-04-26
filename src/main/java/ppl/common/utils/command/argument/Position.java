package ppl.common.utils.command.argument;

import ppl.common.utils.string.Strings;
import ppl.common.utils.command.argument.collector.Collectors;
import ppl.common.utils.command.argument.map.Mappers;

import java.util.*;

public class Position<V> extends BaseArgument<V> {

    public static Position<String> requiredIdentity(String name) {
        return newBuilder(name)
                .map(Mappers.required())
                .collect(Collectors.one());
    }

    public static Position<String> optionalIdentity(String name) {
        return newBuilder(name)
                .collect(Collectors.one());
    }

    public static Builder<String> newBuilder(String name) {
        return new Builder<String>()
                .withName(name);
    }

    private int position = -1;

    private Position(String name,
                     Splitter splitter,
                     List<Mapper<?, ?>> mappers,
                     Collector<?, ?> collector) {
        super(name, splitter, mappers, collector);
    }

    public void init(int position) {
        if (this.position >= 0) {
            throw new UnsupportedOperationException("Already initialized.");
        }

        if (position < 0) {
            throw new IllegalArgumentException("Position must be non-negative.");
        }
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public Builder<V> with() {
        return new Builder<V>()
                .with(this);
    }

    @Override
    public String toString() {
        return Strings.format(
                "position->{}, name->{}",
                getPosition(), getName());
    }

    public static class Builder<V> extends BaseArgument.Builder<V, Position<V>, Builder<V>> {

        private Builder() {}

        @Override
        public Builder<V> split(Splitter splitter) {
            return (Builder<V>) super.split(splitter);
        }

        @Override
        public <R> Builder<R> map(Mapper<V, R> mapper) {
            return (Builder<R>) super.map(mapper);
        }

        @Override
        public Position<V> collect() {
            return (Position<V>) super.collect();
        }

        @Override
        public <R> Position<R> collect(Collector<V, R> collector) {
            return (Position<R>) super.collect(collector);
        }

        protected Position<V> build() {
            return new Position<>(getName(), getSplitter(), getMappers(), getCollector());
        }
    }
}
