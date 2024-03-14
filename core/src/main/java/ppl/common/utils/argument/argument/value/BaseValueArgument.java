package ppl.common.utils.argument.argument.value;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class BaseValueArgument<V> extends ValueArgument<V> {

    protected BaseValueArgument(String name,
                                Function<String, Stream<String>> splitter,
                                @SuppressWarnings("rawtypes") List mappers,
                                @SuppressWarnings("rawtypes") Collector collector,
                                Function<V, String> valueNormalizer) {
        super(name, splitter, mappers, collector, valueNormalizer);
    }

    @Override
    public String keyString() {
        return name();
    }

    public static ValueArgumentBuilder<String> newBuilder(String name) {
        return new Builder<>(name);
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
            A ret = (A) new BaseValueArgument<>(name, splitter, mappers, collector, valueNormalizer);
            return ret;
        }

    }

}
