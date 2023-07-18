package ppl.common.utils.argument;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ToCanonicalString<K, V, A extends AbstractArgument<K, V>> {

    private final Function<A, String> key;
    private final Function<V, String> value;
    private final String separator;
    private final boolean ignoreSeparatorIfNullValue;

    private ToCanonicalString(Function<A, String> key,
                              Function<V, String> value,
                              String separator,
                              boolean ignoreSeparatorIfNullValue) {
        this.key = key;
        this.value = value;
        this.separator = separator;
        this.ignoreSeparatorIfNullValue = ignoreSeparatorIfNullValue;
    }

    public static <K, V, A extends AbstractArgument<K, V>> Builder<K, V, A> newBuilder(String separator, boolean ignoreSeparatorIfNullValue) {
        return new Builder<>(separator, ignoreSeparatorIfNullValue);
    }

    public static class Builder<K, V, A extends AbstractArgument<K, V>> {
        private Function<A, String> key = a -> a.getName().toString();
        private Function<V, String> value = Object::toString;
        private final String separator;
        private final boolean ignoreSeparatorIfNullValue;

        private Builder(String separator, boolean ignoreSeparatorIfNullValue) {
            this.separator = separator;
            this.ignoreSeparatorIfNullValue = ignoreSeparatorIfNullValue;
        }

        public Builder<K, V, A> withKey(Function<A, String> key) {
            this.key = key;
            return this;
        }

        public Builder<K, V, A> withValue(Function<V, String> value) {
            this.value = value;
            return this;
        }

        public BiFunction<A, V, String> build() {
            return new ToCanonicalString<>(key, value, separator, ignoreSeparatorIfNullValue).create();
        }

    }

    private BiFunction<A, V, String> create() {
        return (a, v) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(key.apply(a));
            if (v == null) {
                if (!ignoreSeparatorIfNullValue) {
                    builder.append(separator);
                }
                return builder.toString();
            }
            return builder.append(separator)
                    .append(value.apply(v))
                    .toString();
        };
    }

}
