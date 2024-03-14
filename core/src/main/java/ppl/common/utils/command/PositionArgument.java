package ppl.common.utils.command;

import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.argument.argument.value.ValueArgumentBuilder;
import ppl.common.utils.string.Strings;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class PositionArgument<V> extends ValueArgument<V> {

    public static Builder<String> newBuilder(String name) {
        return new Builder<>(name);
    }

    private int position = -1;

    private PositionArgument(String name,
                             Function<String, Stream<String>> splitter,
                             @SuppressWarnings("rawtypes") List mappers,
                             @SuppressWarnings("rawtypes") Collector collector,
                             Function<V, String> valueNormalizer) {
        super(name, splitter, mappers, collector, valueNormalizer);
    }

    @Override
    public String keyString() {
        return "";
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
                getPosition(), name());
    }

    public static class Builder<V> extends ValueArgumentBuilder<V> {
        private Builder(String name) {
            super(name);
        }

        @Override
        protected <A extends ValueArgument<V>> A create(
                String name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                Function<V, String> valueNormalizer) {
            @SuppressWarnings("unchecked")
            A ret = (A) new PositionArgument<>(name,
                    splitter, mappers,
                    collector, valueNormalizer);
            return ret;
        }
    }
}
