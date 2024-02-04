package ppl.common.utils.argument.value;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class BaseValueArgument<K, V> extends ValueArgument<K, V> {

    public static <K, V> TypeReference<BaseValueArgument<K, V>> ref() {
        @SuppressWarnings("unchecked")
        TypeReference<BaseValueArgument<K, V>> res = (TypeReference<BaseValueArgument<K, V>>) TypeReference.TYPE_REFERENCE;
        return res;
    }

    protected BaseValueArgument(K name,
                                Function<String, Stream<String>> splitter,
                                @SuppressWarnings("rawtypes") List mappers,
                                @SuppressWarnings("rawtypes") Collector collector,
                                BiFunction<BaseValueArgument<K, V>, V, String> toCanonicalString) {
        super(name, splitter, mappers, collector, toCanonicalString);
    }

    public static <K> ValueArgumentBuilder<K, String, BaseValueArgument<K, String>> newBuilder(K name) {
        return new Builder<>(name);
    }

    public static class Builder<K, V> extends ValueArgumentBuilder<K, V, BaseValueArgument<K, V>> {

        public Builder(K name) {
            super(name);
        }

        @Override
        protected BaseValueArgument<K, V> create(
                K name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                BiFunction<BaseValueArgument<K, V>, V, String> toCanonicalString) {
            return new BaseValueArgument<>(name, splitter, mappers, collector, toCanonicalString);
        }
    }

}
