package ppl.common.utils.command;

import ppl.common.utils.argument.argument.value.ValueArgumentNormalizer;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.argument.argument.value.ValueArgumentBuilder;
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

    public static class Builder<V> extends ValueArgumentBuilder<String, V> {
        private Builder(String name) {
            super(name);
        }

        @Override
        protected <A extends ValueArgument<String, V>> A create(
                String name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                BiFunction<A, V, String> toCanonicalString) {
            BiFunction<PositionArgument<V>, V, String> bi = defToCanonical();
            @SuppressWarnings("unchecked")
            BiFunction<PositionArgument<V>, V, String> in = (BiFunction<PositionArgument<V>, V, String>) toCanonicalString;
            @SuppressWarnings("unchecked")
            A ret = (A) new PositionArgument<>(name,
                    splitter, mappers,
                    collector, toCanonicalString == null ? bi : in);
            return ret;
        }
    }
}
