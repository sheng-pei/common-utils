package ppl.common.utils.argument;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ToCanonicalString<K, V> {

    private final Function<K, String> key;
    private final Function<V, String> value;
    private final String separator;
    private final boolean ignoreSeparatorIfNullValue;

    private ToCanonicalString(Function<K, String> key,
                              Function<V, String> value,
                              String separator,
                              boolean ignoreSeparatorIfNullValue) {
        this.key = key;
        this.value = value;
        this.separator = separator;
        this.ignoreSeparatorIfNullValue = ignoreSeparatorIfNullValue;
    }

    public static <K, V> Builder<K, V> newBuilder(String separator, boolean ignoreSeparatorIfNullValue) {
        return new Builder<>(separator, ignoreSeparatorIfNullValue);
    }

    public static class Builder<K, V> {
        private Function<K, String> key = Object::toString;
        private Function<V, String> value = Object::toString;
        private String separator;
        private boolean ignoreSeparatorIfNullValue;

        private Builder(String separator, boolean ignoreSeparatorIfNullValue) {
            this.separator = separator;
            this.ignoreSeparatorIfNullValue = ignoreSeparatorIfNullValue;
        }

        public Builder<K, V> copySeparator() {
            return new Builder<>(separator, ignoreSeparatorIfNullValue);
        }

        public Builder<K, V> withSeparator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder<K, V> withIgnoreSeparatorIfNullValue(boolean ignoreSeparatorIfNullValue) {
            this.ignoreSeparatorIfNullValue = ignoreSeparatorIfNullValue;
            return this;
        }

        public Builder<K, V> withKey(Function<K, String> key) {
            this.key = key;
            return this;
        }

        public Builder<K, V> withValue(Function<V, String> value) {
            this.value = value;
            return this;
        }

        public ToCanonicalString<K, V> build() {
            return new ToCanonicalString<>(key, value, separator, ignoreSeparatorIfNullValue);
        }

    }

    public BiFunction<K, V, String> create() {
        return (k, v) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(key.apply(k));
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
