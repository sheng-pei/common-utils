package ppl.common.utils.argument;

import ppl.common.utils.command.Position;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class BaseArgument<K, V> extends AbstractArgument<K, V> {

    public static <K, V> TypeReference<BaseArgument<K, V>> ref() {
        @SuppressWarnings("unchecked")
        TypeReference<BaseArgument<K, V>> res = (TypeReference<BaseArgument<K, V>>) TypeReference.TYPE_REFERENCE;
        return res;
    }

    protected BaseArgument(K name,
                           Function<String, Stream<String>> splitter,
                           @SuppressWarnings("rawtypes") List mappers,
                           @SuppressWarnings("rawtypes") Collector collector,
                           BiFunction<BaseArgument<K, V>, V, String> toCanonicalString) {
        super(name, splitter, mappers, collector, toCanonicalString);
    }

    public static <K> AbstractBuilder<K, String, BaseArgument<K, String>> newBuilder(K name) {
        return new Builder<>(name);
    }

    public static class Builder<K, V> extends AbstractBuilder<K, V, BaseArgument<K, V>> {

        public Builder(K name) {
            super(name);
        }

        @Override
        protected BaseArgument<K, V> create(
                K name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                BiFunction<BaseArgument<K, V>, V, String> toCanonicalString) {
            return new BaseArgument<>(name, splitter, mappers, collector, toCanonicalString);
        }
    }

}
