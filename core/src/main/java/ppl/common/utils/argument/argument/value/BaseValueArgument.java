package ppl.common.utils.argument.argument.value;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class BaseValueArgument<K, V> extends ValueArgument<K, V> {

    protected BaseValueArgument(K name,
                                Function<String, Stream<String>> splitter,
                                @SuppressWarnings("rawtypes") List mappers,
                                @SuppressWarnings("rawtypes") Collector collector,
                                BiFunction<BaseValueArgument<K, V>, V, String> toCanonicalString) {
        super(name, splitter, mappers, collector, toCanonicalString);
    }

    public static <K> ValueArgumentBuilder<K, String> newBuilder(K name) {
        return new Builder<>(name);
    }

    public static class Builder<K, V> extends ValueArgumentBuilder<K, V> {

        private Builder(K name) {
            super(name);
        }

        @Override
        protected <A extends ValueArgument<K, V>> A create(K name, Function<String, Stream<String>> splitter, List<?> mappers, Collector<?, ?, ?> collector, BiFunction<A, V, String> toCanonicalString) {
            @SuppressWarnings("unchecked")
            BiFunction<BaseValueArgument<K, V>, V, String> in = (BiFunction<BaseValueArgument<K, V>, V, String>) toCanonicalString;
            @SuppressWarnings("unchecked")
            A ret = (A) new BaseValueArgument<>(name, splitter, mappers, collector, in);
            return ret;
        }
    }

}
