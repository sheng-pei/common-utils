package ppl.common.utils.argument;

import java.util.Objects;

public class ArgumentValue<K, V> {
    private final Argument<K, V> argument;
    private final V value;

    public static <K, V> ArgumentValue<K, V> create(Argument<K, V> argument, V value) {
        Objects.requireNonNull(argument, "Argument is required.");
        return new ArgumentValue<>(argument, value);
    }

    ArgumentValue(Argument<K, V> argument, V value) {
        this.argument = argument;
        this.value = value;
    }

    public boolean isKnown() {
        return argument != null;
    }

    public K key() {
        ensureKnown();
        return this.argument.getName();
    }

    public V value() {
        ensureKnown();
        return value;
    }

    public ArgumentValue<K, V> merge(ArgumentValue<K, V> o) {
        ensureKnown();
        if (argument != o.argument) {
            throw new IllegalArgumentException("Required same argument.");
        }
        return new ArgumentValue<>(argument, argument.merge(value, o.value));
    }

    private void ensureKnown() {
        if (!isKnown()) {
            throw new UnsupportedOperationException(
                    "Call value method of unknown argument is not supported.");
        }
    }

    @Override
    public String toString() {
        if (!isKnown()) {
            return value.toString();
        }
        return argument.toCanonicalString(value);
    }
}
