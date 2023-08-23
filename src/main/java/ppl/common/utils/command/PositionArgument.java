package ppl.common.utils.command;

import ppl.common.utils.argument.value.ValueArgumentNormalizer;
import ppl.common.utils.argument.value.ValueArgument;
import ppl.common.utils.argument.value.ValueArgumentBuilder;
import ppl.common.utils.argument.value.TypeReference;
import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class PositionArgument<V> extends ValueArgument<String, V> {

    @SuppressWarnings({"rawtypes"})
    private static final BiFunction DEFAULT_TO_CANONICAL_STRING =
            ValueArgumentNormalizer.newBuilder("", true)
                    .withKey(p -> "")
                    .build();

    public static <V> TypeReference<PositionArgument<V>> ref() {
        @SuppressWarnings("unchecked")
        TypeReference<PositionArgument<V>> res = (TypeReference<PositionArgument<V>>) TypeReference.TYPE_REFERENCE;
        return res;
    }

    public static <V> BiFunction<PositionArgument<V>, V, String> newToCanonical(Function<V, String> value) {
        return ValueArgumentNormalizer.<String, V, PositionArgument<V>>newBuilder("", true)
                .withKey(p -> "")
                .withValue(value)
                .build();
    }

    public static <V> BiFunction<PositionArgument<V>, V, String> defToCanonical() {
        @SuppressWarnings("unchecked")
        BiFunction<PositionArgument<V>, V, String> res = DEFAULT_TO_CANONICAL_STRING;
        return res;
    }

    public static Builder<String> newBuilder(String name) {
        return new Builder<>(name);
    }

    private int position = -1;

    private PositionArgument(String name,
                             Function<String, Stream<String>> splitter,
                             @SuppressWarnings("rawtypes") List mappers,
                             @SuppressWarnings("rawtypes") Collector collector,
                             BiFunction<PositionArgument<V>, V, String> toCanonicalString) {
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

    public static class Builder<V> extends ValueArgumentBuilder<String, V, PositionArgument<V>> {
        private Builder(String name) {
            super(name);
        }

        @Override
        protected PositionArgument<V> create(
                String name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                BiFunction<PositionArgument<V>, V, String> toCanonicalString) {
            return new PositionArgument<>(name,
                    splitter, mappers,
                    collector, toCanonicalString == null ? defToCanonical() : toCanonicalString);
        }
    }
}
