package ppl.common.utils.command;

import ppl.common.utils.argument.*;
import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Position<V> extends AbstractArgument<String, V> {

    @SuppressWarnings({"rawtypes"})
    private static final BiFunction DEFAULT_TO_CANONICAL_STRING =
            ToCanonicalString.newBuilder("", true)
                    .withKey(p -> "")
                    .build()
                    .create();

    public static <V> TypeReference<Position<V>> ref() {
        @SuppressWarnings("unchecked")
        TypeReference<Position<V>> res = (TypeReference<Position<V>>) TypeReference.TYPE_REFERENCE;
        return res;
    }

    public static <V> BiFunction<Position<V>, V, String> newToCanonical(Function<V, String> value) {
        return ToCanonicalString.<String, V, Position<V>>newBuilder("", true)
                .withKey(p -> "")
                .withValue(value)
                .build()
                .create();
    }

    public static <V> BiFunction<Position<V>, V, String> defToCanonical() {
        @SuppressWarnings("unchecked")
        BiFunction<Position<V>, V, String> res = DEFAULT_TO_CANONICAL_STRING;
        return res;
    }

    public static Builder<String> newBuilder(String name) {
        return new Builder<>(name);
    }

    private int position = -1;

    private Position(String name,
                     Function<String, Stream<String>> splitter,
                     @SuppressWarnings("rawtypes") List mappers,
                     @SuppressWarnings("rawtypes") Collector collector,
                     BiFunction<Position<V>, V, String> toCanonicalString) {
        super(name, splitter, mappers, collector, toCanonicalString);
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

    @Override
    public String toString() {
        return Strings.format(
                "position->{}, name->{}",
                getPosition(), getName());
    }

    public static class Builder<V> extends AbstractBuilder<String, V, Position<V>> {
        private Builder(String name) {
            super(name);
        }

        @Override
        protected Position<V> create(
                String name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                BiFunction<Position<V>, V, String> toCanonicalString) {
            return new Position<>(name,
                    splitter, mappers,
                    collector, toCanonicalString == null ? defToCanonical() : toCanonicalString);
        }
    }
}
