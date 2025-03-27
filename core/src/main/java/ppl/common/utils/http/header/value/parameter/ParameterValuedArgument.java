package ppl.common.utils.http.header.value.parameter;

import ppl.common.utils.argument.argument.value.ValuedArgument;
import ppl.common.utils.argument.argument.value.ValuedArgumentBuilder;
import ppl.common.utils.http.symbol.Lexer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class ParameterValuedArgument<V> extends ValuedArgument<V> {

    protected ParameterValuedArgument(String name,
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

    public static ValuedArgumentBuilder<String> newBuilder(String name) {
        return new ParameterValuedArgument.Builder<>(name);
    }

    public static class Builder<V> extends ValuedArgumentBuilder<V> {

        private Builder(String name) {
            super(name);
        }

        @Override
        protected <A extends ValuedArgument<V>> A create(
                String name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                Function<V, String> valueNormalizer) {
            @SuppressWarnings("unchecked")
            A ret = (A) new ParameterValuedArgument<>(
                    name, splitter, mappers, collector,
                    (Function<V, String>)v -> Lexer.writeValue(valueNormalizer.apply(v)));
            return ret;
        }

    }

}
